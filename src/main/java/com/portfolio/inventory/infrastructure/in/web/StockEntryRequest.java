package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record StockEntryRequest(
  @NotNull(message = "The product ID is mandatory.")
  Long productId,

  @NotNull(message = "The provider ID is mandatory.")
  Long supplierId,

  @NotNull(message = "Initial stock is mandatory.")
  @PositiveOrZero(message = "The initial stock cannot be negative.")
  Integer initialStock,

  @NotNull(message = "Current existence is mandatory.")
  @PositiveOrZero(message = "Current existence cannot be negative.")
  Integer currentStock,

  @NotNull(message = "The purchase price is mandatory.")
  @PositiveOrZero(message = "The purchase price cannot be negative.")
  BigDecimal purchasePrice,

  @NotNull(message = "The selling price is mandatory.")
  @PositiveOrZero(message = "The selling price cannot be negative.")
  BigDecimal salePrice,

  @NotBlank(message = "The name of the registering user is mandatory.")
  String enteredBy,

  String receiptUrl
) {}