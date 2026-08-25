package com.portfolio.inventory.domain.repository;

import com.portfolio.inventory.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryRepository {
  Category save(Category category);
  Optional<Category> findById(Long id);
  Page<Category> findAll(Pageable pageable);
  void deleteById(Long id);
  boolean existsByName(String name);
}