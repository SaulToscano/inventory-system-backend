package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataStockEntryRepository extends JpaRepository<StockEntryEntity, Long> {
  Page<StockEntryEntity> findByProductId(Long productId, Pageable pageable);
  Page<StockEntryEntity> findBySupplierId(Long supplierId, Pageable pageable);

  @Query("SELECT s FROM StockEntryEntity s WHERE " +
    "(:productId IS NULL OR s.product.id = :productId) AND " +
    "(:supplierId IS NULL OR s.supplier.id = :supplierId) AND " +
    "(:search = '' OR LOWER(s.product.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    "LOWER(s.supplier.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    "LOWER(s.enteredBy) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<StockEntryEntity> searchAndFilterStockEntries(
    @Param("search") String search,
    @Param("productId") Long productId,
    @Param("supplierId") Long supplierId,
    Pageable pageable
  );
}