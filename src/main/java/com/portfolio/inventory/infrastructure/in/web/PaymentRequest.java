package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.domain.model.enums.PaymentMethod;
import java.math.BigDecimal;

public record PaymentRequest(
  BigDecimal amount,
  PaymentMethod method,
  String bankReference
) {}