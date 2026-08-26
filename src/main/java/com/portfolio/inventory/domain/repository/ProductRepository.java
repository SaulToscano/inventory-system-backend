package com.portfolio.inventory.domain.repository;

import com.portfolio.inventory.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {
  Product save(Product product);
  Optional<Product> findById(Long id);
  Page<Product> findAll(Pageable pageable);
  void deleteById(Long id);
  boolean existsByName(String name);

  // Un método extra muy útil para el futuro frontend:
  // "Dame todos los productos de la categoría X"
  Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}