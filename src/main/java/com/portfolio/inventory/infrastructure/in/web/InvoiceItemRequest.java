package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.domain.model.enums.DiscountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record InvoiceItemRequest(
  @NotNull Long stockEntryId, // ¡Aquí está la clave de tu idea!
  @NotNull @Positive Integer quantity,
  @NotNull @Positive BigDecimal unitPrice,
  BigDecimal discount,
  DiscountType discountType
) {}