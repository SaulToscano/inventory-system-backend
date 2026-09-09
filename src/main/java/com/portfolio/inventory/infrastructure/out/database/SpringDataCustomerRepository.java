package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, Long> {
  boolean existsByEmail(String email);

  @Query("SELECT c FROM CustomerEntity c WHERE " +
    "(:search = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    "LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<CustomerEntity> searchCustomers(@Param("search") String search, Pageable pageable);
}