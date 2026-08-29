package com.portfolio.inventory.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProfitReportItem(
  String invoiceNumber,
  LocalDateTime date,
  String productName,
  Integer quantity,
  BigDecimal revenue, // Lo que el cliente pagó
  BigDecimal cost,    // Lo que te costó a ti
  BigDecimal profit   // Tu ganancia neta
) {
  // Constructor especial para JPQL que calcula la ganancia automáticamente
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