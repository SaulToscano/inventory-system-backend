package com.portfolio.inventory.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Lo que devuelve el Due Report
public record DueReportItem(
  String invoiceNumber,
  String customerName,
  LocalDateTime issueDate,
  BigDecimal totalAmount,
  BigDecimal balanceDue
) {}