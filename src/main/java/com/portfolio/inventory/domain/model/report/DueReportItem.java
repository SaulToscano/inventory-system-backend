package com.portfolio.inventory.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DueReportItem(
  String invoiceNumber,
  String customerName,
  LocalDateTime issueDate,
  BigDecimal totalAmount,
  BigDecimal balanceDue
) {}