package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.NotBlank;

// Los records de Java son ideales para DTOs: inmutables y sin boilerplate
public record CategoryRequest(
  @NotBlank(message = "El nombre es obligatorio") String name,
  String description
) {}