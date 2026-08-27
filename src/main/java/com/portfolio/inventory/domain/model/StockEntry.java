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

  // Relaciones (La categoría ya viene dentro del producto)
  private Product product;
  private Supplier supplier;

  // Comprobante
  private String receiptUrl;

  // Cantidades
  private Integer initialStock;
  private Integer currentStock;

  // Finanzas (BigDecimal es obligatorio para manejar dinero en Java)
  private BigDecimal purchasePrice;
  private BigDecimal salePrice;

  // Auditoría
  private String enteredBy;
  private LocalDateTime entryDate;
}