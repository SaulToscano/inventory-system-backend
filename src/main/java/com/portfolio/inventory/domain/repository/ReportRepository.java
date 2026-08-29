package com.portfolio.inventory.domain.repository;

import com.portfolio.inventory.domain.model.report.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository {
  List<DueReportItem> getDueReport(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo);
  List<SellReportItem> getSellReport(Long categoryId, Long productId, Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo);
  List<StockReportItem> getStockReport(Long categoryId, Long productId, Long supplierId, LocalDateTime dateFrom, LocalDateTime dateTo);
  List<ProfitReportItem> getProfitReport(Long categoryId, Long productId, Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo);
  List<InvoiceReportItem> getInvoiceReport(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo);
}