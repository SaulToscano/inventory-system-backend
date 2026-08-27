package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, Long> {
  boolean existsByEmail(String email);
}