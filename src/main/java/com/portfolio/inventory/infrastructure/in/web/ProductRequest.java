package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
  @NotBlank(message = "The product name is mandatory.") String name,
  String details,
  @NotNull(message = "The category ID is mandatory.") Long categoryId
) {}