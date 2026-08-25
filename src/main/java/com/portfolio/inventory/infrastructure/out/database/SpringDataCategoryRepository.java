package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCategoryRepository extends JpaRepository<CategoryEntity, Long> {
  boolean existsByName(String name);
}