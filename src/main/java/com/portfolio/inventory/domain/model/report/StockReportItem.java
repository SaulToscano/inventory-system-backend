package com.portfolio.inventory.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockReportItem(
  Long stockEntryId,
  String productName,
  String categoryName,
  String supplierName,
  Integer initialStock,
  Integer currentStock,
  BigDecimal purchasePrice,
  BigDecimal salePrice,
  LocalDateTime entryDate
) {}