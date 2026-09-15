package com.portfolio.inventory.domain.model;

import com.portfolio.inventory.domain.model.enums.InvoiceStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Invoice {
  private Long id;
  private String invoiceNumber;
  private LocalDateTime issueDate;

  private Customer customer;
  private List<InvoiceItem> items;
  private List<Payment> payments;

  private BigDecimal totalGross;
  private BigDecimal totalDiscount;
  private BigDecimal netAmount;
  private BigDecimal balanceDue;

  private InvoiceStatus status;
}