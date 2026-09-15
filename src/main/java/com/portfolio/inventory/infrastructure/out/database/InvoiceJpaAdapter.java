package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.Customer;
import com.portfolio.inventory.domain.model.Invoice;
import com.portfolio.inventory.domain.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InvoiceJpaAdapter implements InvoiceRepository {

  private final SpringDataInvoiceRepository invoiceRepository;

  @Override
  public Invoice save(Invoice invoice) {
    CustomerEntity customerEntity = CustomerEntity.builder()
      .id(invoice.getCustomer().getId())
      .build();

    InvoiceEntity entity = InvoiceEntity.builder()
      .id(invoice.getId())
      .invoiceNumber(invoice.getInvoiceNumber())
      .issueDate(invoice.getIssueDate())
      .customer(customerEntity)
      .totalGross(invoice.getTotalGross())
      .totalDiscount(invoice.getTotalDiscount())
      .netAmount(invoice.getNetAmount())
      .balanceDue(invoice.getBalanceDue())
      .status(invoice.getStatus())
      .build();

    if (invoice.getItems() != null) {
      var itemEntities = invoice.getItems().stream().map(item -> InvoiceItemEntity.builder()
        .id(item.getId())
        .invoice(entity)
        .stockEntry(StockEntryEntity.builder().id(item.getStockEntry().getId()).build())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .discount(item.getDiscount())
        .discountType(item.getDiscountType())
        .subTotal(item.getSubTotal())
        .build()).toList();

      entity.setItems(itemEntities);
    }

    if (invoice.getPayments() != null) {
      var paymentEntities = invoice.getPayments().stream().map(payment -> PaymentEntity.builder()
        .id(payment.getId())
        .invoice(entity)
        .amount(payment.getAmount())
        .paymentDate(payment.getPaymentDate())
        .method(payment.getMethod())
        .bankReference(payment.getBankReference())
        .build()).toList();

      entity.setPayments(paymentEntities);
    }

    InvoiceEntity savedEntity = invoiceRepository.save(entity);

    return toDomain(savedEntity);
  }

  @Override
  public Optional<Invoice> findById(Long id) {
    return invoiceRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Page<Invoice> findAll(Pageable pageable) {
    return invoiceRepository.findAll(pageable).map(this::toDomain);
  }

  @Override
  public Page<Invoice> searchInvoices(String search, Long customerId, Long productId, Pageable pageable) {
    return invoiceRepository.searchInvoices(search, customerId, productId, pageable)
      .map(this::toDomain);
  }

  private Invoice toDomain(InvoiceEntity entity) {
    Customer customer = Customer.builder()
      .id(entity.getCustomer().getId())
      .name(entity.getCustomer().getName())
      .email(entity.getCustomer().getEmail())
      .build();

    java.util.List<com.portfolio.inventory.domain.model.InvoiceItem> items = new java.util.ArrayList<>();
    if (entity.getItems() != null) {
      items = entity.getItems().stream().map(itemEntity -> {

        String productName = "Producto (No cargado en esta transacción)";
        if (itemEntity.getStockEntry() != null && itemEntity.getStockEntry().getProduct() != null) {
          productName = itemEntity.getStockEntry().getProduct().getName();
        }

        com.portfolio.inventory.domain.model.Product product = com.portfolio.inventory.domain.model.Product.builder()
          .name(productName)
          .build();

        com.portfolio.inventory.domain.model.StockEntry stockEntry = com.portfolio.inventory.domain.model.StockEntry.builder()
          .id(itemEntity.getStockEntry().getId())
          .product(product)
          .build();

        return com.portfolio.inventory.domain.model.InvoiceItem.builder()
          .id(itemEntity.getId())
          .stockEntry(stockEntry)
          .quantity(itemEntity.getQuantity())
          .unitPrice(itemEntity.getUnitPrice())
          .discount(itemEntity.getDiscount())
          .discountType(itemEntity.getDiscountType())
          .subTotal(itemEntity.getSubTotal())
          .build();
      }).collect(java.util.stream.Collectors.toList());
    }

    java.util.List<com.portfolio.inventory.domain.model.Payment> payments = new java.util.ArrayList<>();
    if (entity.getPayments() != null) {
      payments = entity.getPayments().stream().map(paymentEntity ->
        com.portfolio.inventory.domain.model.Payment.builder()
          .id(paymentEntity.getId())
          .amount(paymentEntity.getAmount())
          .method(paymentEntity.getMethod())
          .paymentDate(paymentEntity.getPaymentDate())
          .bankReference(paymentEntity.getBankReference())
          .build()
      ).collect(java.util.stream.Collectors.toList());
    }

    return Invoice.builder()
      .id(entity.getId())
      .invoiceNumber(entity.getInvoiceNumber())
      .issueDate(entity.getIssueDate())
      .customer(customer)
      .totalGross(entity.getTotalGross())
      .totalDiscount(entity.getTotalDiscount())
      .netAmount(entity.getNetAmount())
      .balanceDue(entity.getBalanceDue())
      .status(entity.getStatus())
      .items(items)
      .payments(payments)
      .build();
  }
}