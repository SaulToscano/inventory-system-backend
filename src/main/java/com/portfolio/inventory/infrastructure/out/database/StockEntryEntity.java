package com.portfolio.inventory.infrastructure.out.database;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockEntryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Llave foránea hacia productos
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  // Llave foránea hacia proveedores
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_id", nullable = false)
  private SupplierEntity supplier;

  private String receiptUrl;

  @Column(nullable = false)
  private Integer initialStock;

  @Column(nullable = false)
  private Integer currentStock;

  @Column(nullable = false, precision = 10, scale = 2) // 10 dígitos totales, 2 decimales
  private BigDecimal purchasePrice;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal salePrice;

  @Column(nullable = false)
  private String enteredBy;

  @Column(nullable = false)
  private LocalDateTime entryDate;
}