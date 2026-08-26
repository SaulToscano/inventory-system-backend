package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataSupplierRepository extends JpaRepository<SupplierEntity, Long> {
  boolean existsByName(String name);
}