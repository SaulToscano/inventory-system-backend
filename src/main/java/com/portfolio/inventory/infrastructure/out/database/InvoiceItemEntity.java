package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id")
  private InvoiceEntity invoice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_entry_id", nullable = false)
  private StockEntryEntity stockEntry;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private BigDecimal unitPrice;

  private BigDecimal discount;

  @Enumerated(EnumType.STRING)
  private DiscountType discountType;

  @Column(nullable = false)
  private BigDecimal subTotal;
}