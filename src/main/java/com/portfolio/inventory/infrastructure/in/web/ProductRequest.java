package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
  @NotBlank(message = "El nombre del producto es obligatorio") String name,
  String details,
  @NotNull(message = "El ID de la categoría es obligatorio") Long categoryId
) {}