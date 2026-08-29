package com.portfolio.inventory.application;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.Invoice;
import com.portfolio.inventory.domain.model.InvoiceItem;
import com.portfolio.inventory.domain.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

  private final InvoiceRepository invoiceRepository;

  public byte[] generateInvoicePdf(Long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
      .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));

    // Usamos ByteArrayOutputStream para guardar el PDF en la memoria RAM
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4);
      PdfWriter.getInstance(document, baos);
      document.open();

      // 1. Título
      Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
      Paragraph title = new Paragraph("FACTURA COMERCIAL", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(20);
      document.add(title);

      // 2. Datos generales
      document.add(new Paragraph("No. Factura: " + invoice.getInvoiceNumber()));
      document.add(new Paragraph("Fecha: " + invoice.getIssueDate().toLocalDate()));
      document.add(new Paragraph("Cliente: " + invoice.getCustomer().getName()));
      document.add(new Paragraph("Correo: " + invoice.getCustomer().getEmail()));
      document.add(new Paragraph("Estado: " + invoice.getStatus().name()));
      document.add(new Paragraph(" ")); // Espacio en blanco

      // 3. Tabla de Productos (4 columnas)
      PdfPTable table = new PdfPTable(4);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{4f, 1f, 2f, 2f}); // Proporción del ancho de columnas

      // Encabezados de la tabla
      String[] headers = {"Producto", "Cant.", "Precio Unit.", "Subtotal"};
      for (String header : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
      }

      // Filas de productos
      for (InvoiceItem item : invoice.getItems()) {
        table.addCell(item.getStockEntry().getProduct().getName());
        table.addCell(String.valueOf(item.getQuantity()));
        table.addCell("$" + item.getUnitPrice().toString());
        table.addCell("$" + item.getSubTotal().toString());
      }
      document.add(table);
      document.add(new Paragraph(" ")); // Espacio

      // 4. Totales
      Paragraph totals = new Paragraph();
      totals.setAlignment(Element.ALIGN_RIGHT);
      totals.add("Subtotal: $" + invoice.getTotalGross() + "\n");
      totals.add("Descuentos: $" + invoice.getTotalDiscount() + "\n");
      totals.add(new Chunk("Total a Pagar: $" + invoice.getNetAmount() + "\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
      totals.add("Saldo Pendiente: $" + invoice.getBalanceDue());
      document.add(totals);

      // 5. Historial de Pagos (NUEVO BLOQUE)
      if (invoice.getPayments() != null && !invoice.getPayments().isEmpty()) {
        document.add(new Paragraph(" ")); // Espacio en blanco
        document.add(new Paragraph(" "));

        Paragraph paymentTitle = new Paragraph("Historial de Pagos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        paymentTitle.setSpacingAfter(10);
        document.add(paymentTitle);

        // Creamos una tabla de 4 columnas
        PdfPTable paymentTable = new PdfPTable(4);
        paymentTable.setWidthPercentage(100);
        paymentTable.setWidths(new float[]{3f, 2f, 3f, 2f});

        // Encabezados de la tabla de pagos
        String[] paymentHeaders = {"Fecha", "Método", "Referencia", "Monto"};
        for (String header : paymentHeaders) {
          PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
          cell.setBackgroundColor(Color.LIGHT_GRAY);
          cell.setPadding(5);
          paymentTable.addCell(cell);
        }

        // Formateador para que la fecha se vea bonita (Día/Mes/Año Hora:Minuto)
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Llenamos las filas con los abonos realizados
        for (com.portfolio.inventory.domain.model.Payment payment : invoice.getPayments()) {
          paymentTable.addCell(payment.getPaymentDate().format(formatter));
          paymentTable.addCell(payment.getMethod().name());

          // Si pagó en efectivo, no hay referencia de banco, ponemos "N/A"
          String ref = (payment.getBankReference() != null && !payment.getBankReference().isBlank())
            ? payment.getBankReference()
            : "N/A";
          paymentTable.addCell(ref);

          paymentTable.addCell("$" + payment.getAmount().toString());
        }
        document.add(paymentTable);
      }

      document.close();
      return baos.toByteArray(); // Retorna el PDF en formato de bytes

    } catch (Exception e) {
      throw new RuntimeException("Error al generar el PDF de la factura", e);
    }
  }
}