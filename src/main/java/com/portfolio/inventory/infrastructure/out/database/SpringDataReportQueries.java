package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.report.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringDataReportQueries extends JpaRepository<InvoiceEntity, Long> {

  // 1. DUE REPORT (Cuentas por cobrar)
  @Query("SELECT new com.portfolio.inventory.domain.model.report.DueReportItem(" +
    "i.invoiceNumber, c.name, i.issueDate, i.netAmount, i.balanceDue) " +
    "FROM InvoiceEntity i JOIN i.customer c " +
    "WHERE i.balanceDue > 0 " +
    "AND (:customerId IS NULL OR c.id = :customerId) " +
    "AND (cast(:dateFrom as timestamp) IS NULL OR i.issueDate >= :dateFrom) " +
    "AND (cast(:dateTo as timestamp) IS NULL OR i.issueDate <= :dateTo) " +
    "ORDER BY i.issueDate DESC")
  List<DueReportItem> generateDueReport(
    @Param("customerId") Long customerId,
    @Param("dateFrom") LocalDateTime dateFrom,
    @Param("dateTo") LocalDateTime dateTo);

  // 2. SELL REPORT (Reporte de ventas detallado)
  @Query("SELECT new com.portfolio.inventory.domain.model.report.SellReportItem(" +
    "i.invoiceNumber, i.issueDate, c.name, p.name, item.quantity, item.unitPrice, item.subTotal) " +
    "FROM InvoiceItemEntity item " +
    "JOIN item.invoice i JOIN i.customer c " +
    "JOIN item.stockEntry se JOIN se.product p " +
    "WHERE (:customerId IS NULL OR c.id = :customerId) " +
    "AND (:productId IS NULL OR p.id = :productId) " +
    "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
    "AND (cast(:dateFrom as timestamp) IS NULL OR i.issueDate >= :dateFrom) " +
    "AND (cast(:dateTo as timestamp) IS NULL OR i.issueDate <= :dateTo) " +
    "ORDER BY i.issueDate DESC")
  List<SellReportItem> generateSellReport(
    @Param("categoryId") Long categoryId,
    @Param("productId") Long productId,
    @Param("customerId") Long customerId,
    @Param("dateFrom") LocalDateTime dateFrom,
    @Param("dateTo") LocalDateTime dateTo);

  // 3. STOCK REPORT (Reporte de existencias por lotes)
  @Query("SELECT new com.portfolio.inventory.domain.model.report.StockReportItem(" +
    "se.id, p.name, c.name, s.name, se.initialStock, se.currentStock, se.purchasePrice, se.salePrice, se.entryDate) " +
    "FROM StockEntryEntity se " +
    "JOIN se.product p " +
    "JOIN p.category c " +
    "JOIN se.supplier s " +
    "WHERE (:productId IS NULL OR p.id = :productId) " +
    "AND (:categoryId IS NULL OR c.id = :categoryId) " +
    "AND (:supplierId IS NULL OR s.id = :supplierId) " +
    "AND (cast(:dateFrom as timestamp) IS NULL OR se.entryDate >= :dateFrom) " +
    "AND (cast(:dateTo as timestamp) IS NULL OR se.entryDate <= :dateTo) " +
    "ORDER BY se.entryDate DESC")
  List<StockReportItem> generateStockReport(
    @Param("categoryId") Long categoryId,
    @Param("productId") Long productId,
    @Param("supplierId") Long supplierId,
    @Param("dateFrom") LocalDateTime dateFrom,
    @Param("dateTo") LocalDateTime dateTo);

  // 4. PROFIT REPORT (Reporte de Ganancias Netas)
  @Query("SELECT new com.portfolio.inventory.domain.model.report.ProfitReportItem(" +
    "i.invoiceNumber, i.issueDate, p.name, item.quantity, item.subTotal, se.purchasePrice) " +
    "FROM InvoiceItemEntity item " +
    "JOIN item.invoice i JOIN i.customer c " +
    "JOIN item.stockEntry se JOIN se.product p JOIN p.category cat " +
    "WHERE (:customerId IS NULL OR c.id = :customerId) " +
    "AND (:productId IS NULL OR p.id = :productId) " +
    "AND (:categoryId IS NULL OR cat.id = :categoryId) " +
    "AND (cast(:dateFrom as timestamp) IS NULL OR i.issueDate >= :dateFrom) " +
    "AND (cast(:dateTo as timestamp) IS NULL OR i.issueDate <= :dateTo) " +
    "ORDER BY i.issueDate DESC")
  List<ProfitReportItem> generateProfitReport(
    @Param("categoryId") Long categoryId,
    @Param("productId") Long productId,
    @Param("customerId") Long customerId,
    @Param("dateFrom") LocalDateTime dateFrom,
    @Param("dateTo") LocalDateTime dateTo);

  // 5. INVOICE REPORT (Resumen General de Facturas)
  @Query("SELECT new com.portfolio.inventory.domain.model.report.InvoiceReportItem(" +
    "i.invoiceNumber, i.issueDate, c.name, i.totalGross, i.totalDiscount, i.netAmount, i.balanceDue, i.status) " +
    "FROM InvoiceEntity i JOIN i.customer c " +
    "WHERE (:customerId IS NULL OR c.id = :customerId) " +
    "AND (cast(:dateFrom as timestamp) IS NULL OR i.issueDate >= :dateFrom) " +
    "AND (cast(:dateTo as timestamp) IS NULL OR i.issueDate <= :dateTo) " +
    "ORDER BY i.issueDate DESC")
  List<InvoiceReportItem> generateInvoiceReport(
    @Param("customerId") Long customerId,
    @Param("dateFrom") LocalDateTime dateFrom,
    @Param("dateTo") LocalDateTime dateTo);
}