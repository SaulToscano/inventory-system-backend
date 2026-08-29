package com.portfolio.inventory.domain.model;

import com.portfolio.inventory.domain.model.enums.DiscountType;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceItem {
  private Long id;
  private StockEntry stockEntry;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal discount;
  private DiscountType discountType;
  private BigDecimal subTotal; // subtotal de esta línea
}