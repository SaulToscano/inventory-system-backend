package com.portfolio.inventory.domain.model.report;

import com.portfolio.inventory.domain.model.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceReportItem(
  String invoiceNumber,
  LocalDateTime issueDate,
  String customerName,
  BigDecimal totalGross,
  BigDecimal totalDiscount,
  BigDecimal netAmount,
  BigDecimal balanceDue,
  InvoiceStatus status
) {}