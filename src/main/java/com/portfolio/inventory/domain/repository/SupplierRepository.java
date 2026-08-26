package com.portfolio.inventory.domain.repository;

import com.portfolio.inventory.domain.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SupplierRepository {
  Supplier save(Supplier supplier);
  Optional<Supplier> findById(Long id);
  Page<Supplier> findAll(Pageable pageable);
  void deleteById(Long id);
  boolean existsByName(String name);
}