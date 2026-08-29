package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.application.ReportPdfService;
import com.portfolio.inventory.application.ReportService;
import com.portfolio.inventory.domain.model.report.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Módulo de Reportes y Estadísticas")
public class ReportController {

  private final ReportService reportService;
  private final ReportPdfService reportPdfService;

  @PostMapping("/due")
  @Operation(summary = "Generar reporte de cuentas por cobrar (Deudores)")
  public ResponseEntity<List<DueReportItem>> generateDueReport(@RequestBody ReportFilterRequest filter) {
    return ResponseEntity.ok(reportService.getDueReport(
      filter.customerId(), filter.dateFrom(), filter.dateTo()
    ));
  }

  @PostMapping(value = "/due/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Descargar reporte de cuentas por cobrar en PDF")
  public ResponseEntity<byte[]> downloadDueReportPdf(@RequestBody ReportFilterRequest filter) {
    List<DueReportItem> data = reportService.getDueReport(filter.customerId(), filter.dateFrom(), filter.dateTo());
    return ResponseEntity.ok()
      .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Cuentas_Cobrar.pdf")
      .body(reportPdfService.generateDueReportPdf(data));
  }

  @PostMapping("/sells")
  @Operation(summary = "Generar reporte detallado de ventas")
  public ResponseEntity<List<SellReportItem>> generateSellReport(@RequestBody ReportFilterRequest filter) {
    return ResponseEntity.ok(reportService.getSellReport(
      filter.categoryId(), filter.productId(), filter.customerId(), filter.dateFrom(), filter.dateTo()
    ));
  }

  @PostMapping(value = "/sells/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Descargar reporte de ventas en formato PDF")
  public ResponseEntity<byte[]> generateSellReportPdf(@RequestBody ReportFilterRequest filter) {

    // 1. Obtenemos los datos desde el servicio normal que ya habíamos hecho
    List<SellReportItem> reportData = reportService.getSellReport(
      filter.categoryId(), filter.productId(), filter.customerId(), filter.dateFrom(), filter.dateTo()
    );

    // 2. Mandamos esos datos al dibujante de PDF
    byte[] pdfBytes = reportPdfService.generateSellReportPdf(reportData);

    // 3. Retornamos el archivo para su descarga
    return ResponseEntity.ok()
      .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Ventas.pdf")
      .body(pdfBytes);
  }

  @PostMapping("/stock")
  @Operation(summary = "Generar reporte de existencias (Stock) por lotes y proveedores")
  public ResponseEntity<List<StockReportItem>> generateStockReport(@RequestBody ReportFilterRequest filter) {
    return ResponseEntity.ok(reportService.getStockReport(
      filter.categoryId(),
      filter.productId(),
      filter.supplierId(),
      filter.dateFrom(),
      filter.dateTo()
    ));
  }

  @PostMapping(value = "/stock/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Descargar reporte de existencias en PDF")
  public ResponseEntity<byte[]> downloadStockReportPdf(@RequestBody ReportFilterRequest filter) {
    List<StockReportItem> data = reportService.getStockReport(filter.categoryId(), filter.productId(), filter.supplierId(), filter.dateFrom(), filter.dateTo());
    return ResponseEntity.ok()
      .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Stock.pdf")
      .body(reportPdfService.generateStockReportPdf(data));
  }

  @PostMapping("/profit")
  @Operation(summary = "Generar reporte de ganancias netas (Profit)")
  public ResponseEntity<List<ProfitReportItem>> generateProfitReport(@RequestBody ReportFilterRequest filter) {
    return ResponseEntity.ok(reportService.getProfitReport(
      filter.categoryId(),
      filter.productId(),
      filter.customerId(),
      filter.dateFrom(),
      filter.dateTo()
    ));
  }

  @PostMapping(value = "/profit/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Descargar reporte de ganancias en PDF")
  public ResponseEntity<byte[]> downloadProfitReportPdf(@RequestBody ReportFilterRequest filter) {
    List<ProfitReportItem> data = reportService.getProfitReport(filter.categoryId(), filter.productId(), filter.customerId(), filter.dateFrom(), filter.dateTo());
    return ResponseEntity.ok()
      .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Ganancias.pdf")
      .body(reportPdfService.generateProfitReportPdf(data));
  }

  @PostMapping("/invoices")
  @Operation(summary = "Generar reporte general de facturas emitidas")
  public ResponseEntity<List<InvoiceReportItem>> generateInvoiceReport(@RequestBody ReportFilterRequest filter) {
    return ResponseEntity.ok(reportService.getInvoiceReport(
      filter.customerId(),
      filter.dateFrom(),
      filter.dateTo()
    ));
  }

  @PostMapping(value = "/invoices/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Descargar resumen de facturación en PDF")
  public ResponseEntity<byte[]> downloadInvoiceReportPdf(@RequestBody ReportFilterRequest filter) {
    List<InvoiceReportItem> data = reportService.getInvoiceReport(filter.customerId(), filter.dateFrom(), filter.dateTo());
    return ResponseEntity.ok()
      .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Resumen_Facturas.pdf")
      .body(reportPdfService.generateInvoiceReportPdf(data));
  }
}