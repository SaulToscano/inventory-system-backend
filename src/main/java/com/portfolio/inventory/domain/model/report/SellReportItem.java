package com.portfolio.inventory.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Lo que devuelve el Sell Report
public record SellReportItem(
  String invoiceNumber,
  LocalDateTime date,
  String customerName,
  String productName,
  Integer quantity,
  BigDecimal unitPrice,
  BigDecimal subTotal
) {}