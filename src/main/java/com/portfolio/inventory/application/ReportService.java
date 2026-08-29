package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.model.report.*;
import com.portfolio.inventory.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;

  public List<DueReportItem> getDueReport(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return reportRepository.getDueReport(customerId, dateFrom, dateTo);
  }

  public List<SellReportItem> getSellReport(Long categoryId, Long productId, Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return reportRepository.getSellReport(categoryId, productId, customerId, dateFrom, dateTo);
  }

  public List<StockReportItem> getStockReport(Long categoryId, Long productId, Long supplierId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return reportRepository.getStockReport(categoryId, productId, supplierId, dateFrom, dateTo);
  }

  public List<ProfitReportItem> getProfitReport(Long categoryId, Long productId, Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return reportRepository.getProfitReport(categoryId, productId, customerId, dateFrom, dateTo);
  }

  public List<InvoiceReportItem> getInvoiceReport(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return reportRepository.getInvoiceReport(customerId, dateFrom, dateTo);
  }
}