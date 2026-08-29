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

  /**
   * @Transactional asegura que si ocurre un error (ej. no hay stock),
   * NADA se guarda en la base de datos, evitando inconsistencias.
   */
  @Transactional
  public Invoice generateInvoice(Long customerId, List<InvoiceItem> items, Payment initialPayment) {

    // 1. Validar Cliente
    Customer customer = customerRepository.findById(customerId)
      .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

    // 2. Inicializar totales de la factura
    BigDecimal totalGross = BigDecimal.ZERO;
    BigDecimal totalDiscount = BigDecimal.ZERO;

    // 3. Procesar cada producto (Matemáticas y Validación de Stock)
    for (InvoiceItem item : items) {
      StockEntry stockEntry = stockEntryRepository.findById(item.getStockEntry().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Lote de inventario no encontrado"));

      // Validar existencia
      if (stockEntry.getCurrentStock() < item.getQuantity()) {
        throw new IllegalArgumentException("Stock insuficiente en la entrada seleccionada");
      }

      // Restar stock
      stockEntry.setCurrentStock(stockEntry.getCurrentStock() - item.getQuantity());
      stockEntryRepository.save(stockEntry);

      // Calcular Subtotal del Item (Precio * Cantidad)
      BigDecimal itemGrossAmount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

      // Calcular Descuento del Item
      BigDecimal itemDiscountAmount = calculateItemDiscount(itemGrossAmount, item.getDiscount(), item.getDiscountType());

      // Calcular Neto del Item
      BigDecimal itemNetAmount = itemGrossAmount.subtract(itemDiscountAmount);

      // Setear valores en el item
      item.setStockEntry(stockEntry);
      item.setSubTotal(itemNetAmount);

      // Sumar a los totales de la factura
      totalGross = totalGross.add(itemGrossAmount);
      totalDiscount = totalDiscount.add(itemDiscountAmount);
    }

    // 4. Calcular Total Final de la Factura
    BigDecimal netAmount = totalGross.subtract(totalDiscount);
    BigDecimal balanceDue = netAmount; // Inicialmente, debe todo

    // 5. Construir la Factura Base
    Invoice invoice = Invoice.builder()
      .invoiceNumber("FAC-" + System.currentTimeMillis()) // Generador temporal de folio
      .issueDate(LocalDateTime.now())
      .customer(customer)
      .items(items)
      .totalGross(totalGross)
      .totalDiscount(totalDiscount)
      .netAmount(netAmount)
      .build();

    // 6. Procesar Abono Inicial (Si el cliente pagó algo al momento)
    if (initialPayment != null && initialPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
      if (initialPayment.getAmount().compareTo(netAmount) > 0) {
        throw new IllegalArgumentException("El pago inicial no puede ser mayor al total de la factura");
      }

      initialPayment.setPaymentDate(LocalDateTime.now());
      // Relacionamos el pago con la factura (Si tuvieras setters en dominio)
      invoice.setPayments(List.of(initialPayment));

      balanceDue = netAmount.subtract(initialPayment.getAmount());
    }

    // 7. Determinar Estado de la Factura
    invoice.setBalanceDue(balanceDue);
    if (balanceDue.compareTo(BigDecimal.ZERO) == 0) {
      invoice.setStatus(InvoiceStatus.PAID);
    } else if (balanceDue.compareTo(netAmount) < 0) {
      invoice.setStatus(InvoiceStatus.PARTIAL_PAID);
    } else {
      invoice.setStatus(InvoiceStatus.PENDING);
    }

    // 8. Guardar y retornar (Hibernate hace la cascada para guardar Items y Payments)
    return invoiceRepository.save(invoice);
  }

  /**
   * Helper Method: Calcula el valor monetario de un descuento
   */
  private BigDecimal calculateItemDiscount(BigDecimal grossAmount, BigDecimal discountValue, DiscountType type) {
    if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }

    if (type == DiscountType.FIXED_AMOUNT) {
      return discountValue;
    } else if (type == DiscountType.PERCENTAGE) {
      // Ejemplo: (1000 * 10) / 100 = 100 de descuento
      return grossAmount.multiply(discountValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    return BigDecimal.ZERO;
  }

  @Transactional
  public Invoice registerPayment(Long invoiceId, PaymentRequest paymentRequest) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
      .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));

    if (invoice.getStatus() == InvoiceStatus.PAID) {
      throw new IllegalArgumentException("Esta factura ya está pagada en su totalidad");
    }

    if (paymentRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El monto del abono debe ser mayor a cero");
    }

    if (paymentRequest.amount().compareTo(invoice.getBalanceDue()) > 0) {
      throw new IllegalArgumentException("El abono (" + paymentRequest.amount() +
        ") no puede ser mayor al saldo pendiente (" + invoice.getBalanceDue() + ")");
    }

    // Crear el pago
    Payment payment = Payment.builder()
      .amount(paymentRequest.amount())
      .method(paymentRequest.method())
      .bankReference(paymentRequest.bankReference())
      .paymentDate(LocalDateTime.now())
      .build();

    // Actualizar la factura
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

  public org.springframework.data.domain.Page<Invoice> getAllInvoices(org.springframework.data.domain.Pageable pageable) {
    return invoiceRepository.findAll(pageable);
  }
}