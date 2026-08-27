package com.portfolio.inventory.domain.repository;

import com.portfolio.inventory.domain.model.StockEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StockEntryRepository {
  StockEntry save(StockEntry stockEntry);
  Optional<StockEntry> findById(Long id);
  Page<StockEntry> findAll(Pageable pageable);

  // Útiles para el frontend más adelante
  Page<StockEntry> findByProductId(Long productId, Pageable pageable);
  Page<StockEntry> findBySupplierId(Long supplierId, Pageable pageable);
}