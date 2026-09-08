package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataProductRepository extends JpaRepository<ProductEntity, Long> {

  boolean existsByName(String name);

  Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p WHERE " +
    "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
    "(:search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.details) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<ProductEntity> searchAndFilterProducts(
    @Param("search") String search,
    @Param("categoryId") Long categoryId,
    Pageable pageable
  );
}