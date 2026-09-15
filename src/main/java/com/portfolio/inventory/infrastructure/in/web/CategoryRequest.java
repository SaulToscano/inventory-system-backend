package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
  @NotBlank(message = "The name is mandatory.") String name,
  String description
) {}