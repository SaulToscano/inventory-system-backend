package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
  @NotBlank(message = "The client's name is mandatory.")
  String name,

  @NotBlank(message = "Email is required.")
  @Email(message = "The email format is invalid.")
  String email,

  String address,
  String phone
) {}