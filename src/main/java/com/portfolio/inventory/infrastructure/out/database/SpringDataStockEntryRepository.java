package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataStockEntryRepository extends JpaRepository<StockEntryEntity, Long> {
  Page<StockEntryEntity> findByProductId(Long productId, Pageable pageable);
  Page<StockEntryEntity> findBySupplierId(Long supplierId, Pageable pageable);
}