package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record StockEntryRequest(
  @NotNull(message = "El ID del producto es obligatorio")
  Long productId,

  @NotNull(message = "El ID del proveedor es obligatorio")
  Long supplierId,

  @NotNull(message = "La existencia inicial es obligatoria")
  @PositiveOrZero(message = "La existencia inicial no puede ser negativa")
  Integer initialStock,

  @NotNull(message = "La existencia actual es obligatoria")
  @PositiveOrZero(message = "La existencia actual no puede ser negativa")
  Integer currentStock,

  @NotNull(message = "El precio de compra es obligatorio")
  @PositiveOrZero(message = "El precio de compra no puede ser negativo")
  BigDecimal purchasePrice,

  @NotNull(message = "El precio de venta es obligatorio")
  @PositiveOrZero(message = "El precio de venta no puede ser negativo")
  BigDecimal salePrice,

  @NotBlank(message = "El nombre del usuario que registra es obligatorio")
  String enteredBy,

  // Lo dejamos opcional temporalmente hasta que integremos Supabase Storage
  String receiptUrl
) {}