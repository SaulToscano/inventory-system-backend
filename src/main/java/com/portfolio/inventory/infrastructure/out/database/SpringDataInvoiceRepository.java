package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
  boolean existsByInvoiceNumber(String invoiceNumber);
}
