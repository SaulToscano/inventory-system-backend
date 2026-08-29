package com.portfolio.inventory.infrastructure.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record InvoiceRequest(
  @NotNull Long customerId,
  @NotEmpty @Valid List<InvoiceItemRequest> items,
  PaymentRequest initialPayment // Puede ser null si es a crédito
) {}