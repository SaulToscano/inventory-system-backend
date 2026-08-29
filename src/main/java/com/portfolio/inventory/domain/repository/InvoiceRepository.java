package com.portfolio.inventory.domain.repository;

import com.portfolio.inventory.domain.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface InvoiceRepository {
  Invoice save(Invoice invoice);
  Optional<Invoice> findById(Long id);
  Page<Invoice> findAll(Pageable pageable);
}
