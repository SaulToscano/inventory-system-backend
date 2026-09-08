package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCategoryRepository extends JpaRepository<CategoryEntity, Long> {
  boolean existsByName(String name);

  @Query("SELECT c FROM CategoryEntity c WHERE " +
    "(:search = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<CategoryEntity> searchCategories(@Param("search") String search, Pageable pageable);
}