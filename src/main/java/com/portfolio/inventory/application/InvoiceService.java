package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.*;
import com.portfolio.inventory.domain.model.enums.DiscountType;
import com.portfolio.inventory.domain.model.enums.InvoiceStatus;
import com.portfolio.inventory.domain.repository.CustomerRepository;
import com.portfolio.inventory.domain.repository.InvoiceRepository;
import com.portfolio.inventory.domain.repository.StockEntryRepository;
import com.portfolio.inventory.infrastructure.in.web.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final CustomerRepository customerRepository;
  private final StockEntryRepository stockEntryRepository;

  @Transactional
  public Invoice generateInvoice(Long customerId, LocalDateTime issueDate, List<InvoiceItem> items, Payment initialPayment) {
    Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Client not found"));

    BigDecimal totalGross = BigDecimal.ZERO;
    BigDecimal totalDiscount = BigDecimal.ZERO;

    // Process each product (Calculations and Stock Validation)
    for (InvoiceItem item : items) {
      StockEntry stockEntry = stockEntryRepository.findById(item.getStockEntry().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Inventory batch not found"));

      if (stockEntry.getCurrentStock() < item.getQuantity()) {
        throw new IllegalArgumentException("Insufficient stock at the selected entrance");
      }

      stockEntry.setCurrentStock(stockEntry.getCurrentStock() - item.getQuantity());
      stockEntryRepository.save(stockEntry);

      BigDecimal itemGrossAmount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
      BigDecimal itemDiscountAmount = calculateItemDiscount(itemGrossAmount, item.getDiscount(), item.getDiscountType());
      BigDecimal itemNetAmount = itemGrossAmount.subtract(itemDiscountAmount);

      item.setStockEntry(stockEntry);
      item.setSubTotal(itemNetAmount);

      totalGross = totalGross.add(itemGrossAmount);
      totalDiscount = totalDiscount.add(itemDiscountAmount);
    }

    BigDecimal netAmount = totalGross.subtract(totalDiscount);
    BigDecimal balanceDue = netAmount;

    Invoice invoice = Invoice.builder()
      .invoiceNumber("FAC-" + System.currentTimeMillis())
      .issueDate(issueDate)
      .customer(customer)
      .items(items)
      .totalGross(totalGross)
      .totalDiscount(totalDiscount)
      .netAmount(netAmount)
      .build();

    if (initialPayment != null && initialPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
      if (initialPayment.getAmount().compareTo(netAmount) > 0) {
        throw new IllegalArgumentException("The initial payment cannot exceed the total invoice amount.");
      }

      initialPayment.setPaymentDate(LocalDateTime.now());
      invoice.setPayments(List.of(initialPayment));

      balanceDue = netAmount.subtract(initialPayment.getAmount());
    }

    invoice.setBalanceDue(balanceDue);
    if (balanceDue.compareTo(BigDecimal.ZERO) == 0) {
      invoice.setStatus(InvoiceStatus.PAID);
    } else if (balanceDue.compareTo(netAmount) < 0) {
      invoice.setStatus(InvoiceStatus.PARTIAL_PAID);
    } else {
      invoice.setStatus(InvoiceStatus.PENDING);
    }

    return invoiceRepository.save(invoice);
  }

  private BigDecimal calculateItemDiscount(BigDecimal grossAmount, BigDecimal discountValue, DiscountType type) {
    if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }

    if (type == DiscountType.FIXED_AMOUNT) {
      return discountValue;
    } else if (type == DiscountType.PERCENTAGE) {
      return grossAmount.multiply(discountValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    return BigDecimal.ZERO;
  }

  @Transactional
  public Invoice registerPayment(Long invoiceId, PaymentRequest paymentRequest) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
      .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

    if (invoice.getStatus() == InvoiceStatus.PAID) {
      throw new IllegalArgumentException("This invoice has already been paid in full.");
    }

    if (paymentRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("The payment amount must be greater than zero.");
    }

    if (paymentRequest.amount().compareTo(invoice.getBalanceDue()) > 0) {
      throw new IllegalArgumentException("The payment (" + paymentRequest.amount() +
        ") cannot exceed the outstanding balance (" + invoice.getBalanceDue() + ")");
    }

    Payment payment = Payment.builder()
      .amount(paymentRequest.amount())
      .method(paymentRequest.method())
      .bankReference(paymentRequest.bankReference())
      .paymentDate(LocalDateTime.now())
      .build();

    invoice.getPayments().add(payment);

    BigDecimal newBalanceDue = invoice.getBalanceDue().subtract(payment.getAmount());
    invoice.setBalanceDue(newBalanceDue);

    if (newBalanceDue.compareTo(BigDecimal.ZERO) == 0) {
      invoice.setStatus(InvoiceStatus.PAID);
    } else {
      invoice.setStatus(InvoiceStatus.PARTIAL_PAID);
    }

    return invoiceRepository.save(invoice);
  }

  public org.springframework.data.domain.Page<Invoice> getAllInvoices(String search, Long customerId, Long productId, org.springframework.data.domain.Pageable pageable) {
    String finalSearch = (search != null && !search.trim().isEmpty()) ? search : "";

    return invoiceRepository.searchInvoices(finalSearch, customerId, productId, pageable);
  }
}