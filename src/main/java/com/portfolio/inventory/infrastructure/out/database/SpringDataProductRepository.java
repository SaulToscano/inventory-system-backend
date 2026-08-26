package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataProductRepository extends JpaRepository<ProductEntity, Long> {

  boolean existsByName(String name);

  // Spring Boot infiere la consulta SQL buscando la propiedad "category" y luego su "id"
  Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
}