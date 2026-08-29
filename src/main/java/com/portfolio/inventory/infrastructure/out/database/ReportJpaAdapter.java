package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.report.*;
import com.portfolio.inventory.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportJpaAdapter implements ReportRepository {

  private final SpringDataReportQueries queries;

  @Override
  public List<DueReportItem> getDueReport(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return queries.generateDueReport(customerId, dateFrom, dateTo);
  }

  @Override
  public List<SellReportItem> getSellReport(Long categoryId, Long productId, Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return queries.generateSellReport(categoryId, productId, customerId, dateFrom, dateTo);
  }

  @Override
  public List<StockReportItem> getStockReport(Long categoryId, Long productId, Long supplierId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return queries.generateStockReport(categoryId, productId, supplierId, dateFrom, dateTo);
  }

  @Override
  public List<ProfitReportItem> getProfitReport(Long categoryId, Long productId, Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return queries.generateProfitReport(categoryId, productId, customerId, dateFrom, dateTo);
  }

  @Override
  public List<InvoiceReportItem> getInvoiceReport(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {
    return queries.generateInvoiceReport(customerId, dateFrom, dateTo);
  }
}