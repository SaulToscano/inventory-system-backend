package com.portfolio.inventory.infrastructure.in.web.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorMessage(
  LocalDateTime timestamp,
  int statusCode,
  String error,
  String message,
  String path,
  List<String> details // Útil para listar múltiples errores de validación
) {}