package com.portfolio.inventory.infrastructure.in.web;

import java.time.LocalDateTime;

// Todos son opcionales
public record ReportFilterRequest(
  LocalDateTime dateFrom,
  LocalDateTime dateTo,
  Long categoryId,
  Long productId,
  Long supplierId,
  Long customerId
) {}