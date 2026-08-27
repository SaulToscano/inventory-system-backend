package com.portfolio.inventory.domain.repository;

import com.portfolio.inventory.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerRepository {
  Customer save(Customer customer);
  Optional<Customer> findById(Long id);
  Page<Customer> findAll(Pageable pageable);
  void deleteById(Long id);
  boolean existsByEmail(String email); // Validaremos por email para evitar clientes duplicados
}