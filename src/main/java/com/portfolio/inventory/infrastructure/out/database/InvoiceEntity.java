package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String invoiceNumber;

  @Column(nullable = false)
  private LocalDateTime issueDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private CustomerEntity customer;

  // CascadeType.ALL significa que si guardas la factura, sus items se guardan solos
  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InvoiceItemEntity> items = new ArrayList<>();

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PaymentEntity> payments = new ArrayList<>();

  private BigDecimal totalGross;
  private BigDecimal totalDiscount;
  private BigDecimal netAmount;
  private BigDecimal balanceDue;

  @Enumerated(EnumType.STRING)
  private InvoiceStatus status;
}