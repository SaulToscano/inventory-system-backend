package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
  @NotBlank(message = "El nombre del proveedor es obligatorio") String name,

  // Podemos aprovechar las validaciones de Spring para el formato del correo
  @Email(message = "El formato del correo electrónico no es válido") String email,

  String phone,
  String address
) {}