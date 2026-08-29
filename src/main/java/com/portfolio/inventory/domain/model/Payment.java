package com.portfolio.inventory.domain.model;

import com.portfolio.inventory.domain.model.enums.PaymentMethod;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {
  private Long id;
  private BigDecimal amount;
  private LocalDateTime paymentDate;
  private PaymentMethod method;
  private String bankReference; // Solo si es por banco
}