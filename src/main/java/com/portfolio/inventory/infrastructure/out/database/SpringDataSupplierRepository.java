package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataSupplierRepository extends JpaRepository<SupplierEntity, Long> {
  boolean existsByName(String name);

  @Query("SELECT s FROM SupplierEntity s WHERE " +
    "(:search = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    "LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    "LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<SupplierEntity> searchSuppliers(@Param("search") String search, Pageable pageable);
}