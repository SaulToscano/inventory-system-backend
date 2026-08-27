package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
  @NotBlank(message = "El nombre del cliente es obligatorio")
  String name,

  @NotBlank(message = "El correo electrónico es obligatorio")
  @Email(message = "El formato del correo electrónico no es válido")
  String email,

  String address,
  String phone
) {}