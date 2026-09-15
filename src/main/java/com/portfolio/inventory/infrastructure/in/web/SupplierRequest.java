package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
  @NotBlank(message = "The supplier's name is mandatory.") String name,
  @Email(message = "The email format is invalid.") String email,

  String phone,
  String address
) {}