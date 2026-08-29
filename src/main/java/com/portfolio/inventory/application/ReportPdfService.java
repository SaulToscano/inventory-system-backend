package com.portfolio.inventory.application;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.portfolio.inventory.domain.model.report.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportPdfService {

  public byte[] generateSellReportPdf(List<SellReportItem> items) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

      // Usamos formato Horizontal (LANDSCAPE) porque las tablas de reportes suelen ser anchas
      Document document = new Document(PageSize.A4.rotate());
      PdfWriter.getInstance(document, baos);
      document.open();

      // 1. Título y Encabezado
      Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
      Paragraph title = new Paragraph("REPORTE DETALLADO DE VENTAS", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(10);
      document.add(title);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      Paragraph generatedAt = new Paragraph("Generado el: " + LocalDateTime.now().format(formatter));
      generatedAt.setAlignment(Element.ALIGN_RIGHT);
      generatedAt.setSpacingAfter(20);
      document.add(generatedAt);

      // 2. Definir la Tabla (7 columnas)
      PdfPTable table = new PdfPTable(7);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{2f, 2f, 3f, 3f, 1f, 2f, 2f}); // Proporciones

      // 3. Encabezados
      String[] headers = {"Factura", "Fecha", "Cliente", "Producto", "Cant.", "Precio Unit.", "Subtotal"};
      Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
      for (String header : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
        cell.setBackgroundColor(new Color(220, 220, 220)); // Gris claro
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
      }

      // 4. Llenar filas y calcular totales
      Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
      BigDecimal grandTotal = BigDecimal.ZERO;

      for (SellReportItem item : items) {
        table.addCell(new Phrase(item.invoiceNumber(), rowFont));
        table.addCell(new Phrase(item.date().toLocalDate().toString(), rowFont));
        table.addCell(new Phrase(item.customerName(), rowFont));
        table.addCell(new Phrase(item.productName(), rowFont));

        PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.quantity()), rowFont));
        qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(qtyCell);

        table.addCell(new Phrase("$" + item.unitPrice(), rowFont));
        table.addCell(new Phrase("$" + item.subTotal(), rowFont));

        grandTotal = grandTotal.add(item.subTotal());
      }
      document.add(table);

      // 5. Total Final
      Paragraph totalParagraph = new Paragraph("Gran Total Vendido: $" + grandTotal,
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
      totalParagraph.setAlignment(Element.ALIGN_RIGHT);
      totalParagraph.setSpacingBefore(15);
      document.add(totalParagraph);

      document.close();
      return baos.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Error al generar el PDF del reporte", e);
    }
  }

  public byte[] generateDueReportPdf(List<DueReportItem> items) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4); // Vertical está bien aquí
      PdfWriter.getInstance(document, baos);
      document.open();

      document.add(new Paragraph("REPORTE DE CUENTAS POR COBRAR (DEUDORES)", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
      document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
      document.add(new Paragraph(" "));

      PdfPTable table = new PdfPTable(5);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{2f, 3f, 2f, 2f, 2f});

      String[] headers = {"Factura", "Cliente", "Fecha", "Total Factura", "Saldo Pendiente"};
      for (String h : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
      }

      BigDecimal totalDeuda = BigDecimal.ZERO;
      Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

      for (DueReportItem item : items) {
        table.addCell(new Phrase(item.invoiceNumber(), rowFont));
        table.addCell(new Phrase(item.customerName(), rowFont));
        table.addCell(new Phrase(item.issueDate().toLocalDate().toString(), rowFont));
        table.addCell(new Phrase("$" + item.totalAmount(), rowFont));
        table.addCell(new Phrase("$" + item.balanceDue(), rowFont));
        totalDeuda = totalDeuda.add(item.balanceDue());
      }
      document.add(table);

      Paragraph totalStr = new Paragraph("Deuda Total Pendiente: $" + totalDeuda, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
      totalStr.setAlignment(Element.ALIGN_RIGHT);
      document.add(totalStr);

      document.close();
      return baos.toByteArray();
    } catch (Exception e) { throw new RuntimeException(e); }
  }

  public byte[] generateStockReportPdf(List<StockReportItem> items) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4.rotate()); // Horizontal
      PdfWriter.getInstance(document, baos);
      document.open();

      document.add(new Paragraph("REPORTE DE EXISTENCIAS POR LOTE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
      document.add(new Paragraph(" "));

      PdfPTable table = new PdfPTable(7);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{1f, 3f, 2f, 3f, 1f, 1f, 2f});

      String[] headers = {"Lote ID", "Producto", "Categoría", "Proveedor", "Inicial", "Actual", "Costo Unit."};
      for (String h : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
      }

      Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
      for (StockReportItem item : items) {
        table.addCell(new Phrase(item.stockEntryId().toString(), rowFont));
        table.addCell(new Phrase(item.productName(), rowFont));
        table.addCell(new Phrase(item.categoryName(), rowFont));
        table.addCell(new Phrase(item.supplierName(), rowFont));
        table.addCell(new Phrase(item.initialStock().toString(), rowFont));
        table.addCell(new Phrase(item.currentStock().toString(), rowFont));
        table.addCell(new Phrase("$" + item.purchasePrice(), rowFont));
      }
      document.add(table);
      document.close();
      return baos.toByteArray();
    } catch (Exception e) { throw new RuntimeException(e); }
  }

  public byte[] generateProfitReportPdf(List<ProfitReportItem> items) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4.rotate());
      PdfWriter.getInstance(document, baos);
      document.open();

      document.add(new Paragraph("REPORTE DE GANANCIAS NETAS (PROFIT)", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
      document.add(new Paragraph(" "));

      PdfPTable table = new PdfPTable(7);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{2f, 2f, 3f, 1f, 2f, 2f, 2f});

      String[] headers = {"Factura", "Fecha", "Producto", "Cant.", "Ingreso (Venta)", "Costo Real", "Ganancia Neta"};
      for (String h : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
      }

      BigDecimal totalProfit = BigDecimal.ZERO;
      Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

      for (ProfitReportItem item : items) {
        table.addCell(new Phrase(item.invoiceNumber(), rowFont));
        table.addCell(new Phrase(item.date().toLocalDate().toString(), rowFont));
        table.addCell(new Phrase(item.productName(), rowFont));
        table.addCell(new Phrase(item.quantity().toString(), rowFont));
        table.addCell(new Phrase("$" + item.revenue(), rowFont));
        table.addCell(new Phrase("$" + item.cost(), rowFont));
        table.addCell(new Phrase("$" + item.profit(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));

        totalProfit = totalProfit.add(item.profit());
      }
      document.add(table);

      Paragraph totalStr = new Paragraph("Ganancia Total: $" + totalProfit, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
      totalStr.setAlignment(Element.ALIGN_RIGHT);
      document.add(totalStr);

      document.close();
      return baos.toByteArray();
    } catch (Exception e) { throw new RuntimeException(e); }
  }

  public byte[] generateInvoiceReportPdf(List<InvoiceReportItem> items) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4.rotate());
      PdfWriter.getInstance(document, baos);
      document.open();

      document.add(new Paragraph("RESUMEN DE FACTURACIÓN EMITIDA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
      document.add(new Paragraph(" "));

      PdfPTable table = new PdfPTable(8);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{2f, 2f, 3f, 2f, 1.5f, 2f, 2f, 2f});

      String[] headers = {"Factura", "Fecha", "Cliente", "Subtotal", "Desc.", "Neto", "Pendiente", "Estado"};
      for (String h : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
      }

      BigDecimal grandNet = BigDecimal.ZERO;
      Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

      for (InvoiceReportItem item : items) {
        table.addCell(new Phrase(item.invoiceNumber(), rowFont));
        table.addCell(new Phrase(item.issueDate().toLocalDate().toString(), rowFont));
        table.addCell(new Phrase(item.customerName(), rowFont));
        table.addCell(new Phrase("$" + item.totalGross(), rowFont));
        table.addCell(new Phrase("$" + item.totalDiscount(), rowFont));
        table.addCell(new Phrase("$" + item.netAmount(), rowFont));
        table.addCell(new Phrase("$" + item.balanceDue(), rowFont));
        table.addCell(new Phrase(item.status().name(), rowFont));

        grandNet = grandNet.add(item.netAmount());
      }
      document.add(table);

      Paragraph totalStr = new Paragraph("Ingreso Neto Total: $" + grandNet, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
      totalStr.setAlignment(Element.ALIGN_RIGHT);
      document.add(totalStr);

      document.close();
      return baos.toByteArray();
    } catch (Exception e) { throw new RuntimeException(e); }
  }
}