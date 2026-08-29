package com.portfolio.inventory.domain.model;

import com.portfolio.inventory.domain.model.enums.InvoiceStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Invoice {
  private Long id;
  private String invoiceNumber; // Ej: FAC-2026-0001
  private LocalDateTime issueDate;

  private Customer customer;
  private List<InvoiceItem> items;
  private List<Payment> payments;

  // Totales financieros
  private BigDecimal totalGross; // Subtotal sin descuentos
  private BigDecimal totalDiscount;
  private BigDecimal netAmount; // Total a pagar
  private BigDecimal balanceDue; // Saldo pendiente

  private InvoiceStatus status;
}