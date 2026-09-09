package com.portfolio.inventory.infrastructure.out.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
  boolean existsByInvoiceNumber(String invoiceNumber);

  @Query("SELECT DISTINCT i FROM InvoiceEntity i LEFT JOIN i.items item " +
    "WHERE (:customerId IS NULL OR i.customer.id = :customerId) " +
    "AND (:productId IS NULL OR item.stockEntry.product.id = :productId) " +
    "AND (:search = '' OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<InvoiceEntity> searchInvoices(@Param("search") String search,
                                     @Param("customerId") Long customerId,
                                     @Param("productId") Long productId,
                                     Pageable pageable);
}
