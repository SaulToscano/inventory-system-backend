package com.portfolio.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEntry {

  private Long id;

  private Product product;
  private Supplier supplier;

  private String receiptUrl;

  private Integer initialStock;
  private Integer currentStock;

  private BigDecimal purchasePrice;
  private BigDecimal salePrice;

  private String enteredBy;
  private LocalDateTime entryDate;
}