package com.portfolio.inventory.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProfitReportItem(
  String invoiceNumber,
  LocalDateTime date,
  String productName,
  Integer quantity,
  BigDecimal revenue, // What the customer paid
  BigDecimal cost,    // What it cost you
  BigDecimal profit   // Your net profit
) {
  // Special constructor for JPQL that automatically calculates the profit.
  public ProfitReportItem(String invoiceNumber, LocalDateTime date, String productName, Integer quantity, BigDecimal revenue, BigDecimal unitCost) {
    this(
      invoiceNumber,
      date,
      productName,
      quantity,
      revenue,
      unitCost.multiply(BigDecimal.valueOf(quantity)),
      revenue.subtract(unitCost.multiply(BigDecimal.valueOf(quantity)))
    );
  }
}