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
      .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4);
      PdfWriter.getInstance(document, baos);
      document.open();

      // 1. title
      Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
      Paragraph title = new Paragraph("FACTURA COMERCIAL", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(20);
      document.add(title);

      // 2. General information
      document.add(new Paragraph("No. Factura: " + invoice.getInvoiceNumber()));
      document.add(new Paragraph("Fecha: " + invoice.getIssueDate().toLocalDate()));
      document.add(new Paragraph("Cliente: " + invoice.getCustomer().getName()));
      document.add(new Paragraph("Correo: " + invoice.getCustomer().getEmail()));
      document.add(new Paragraph("Estado: " + invoice.getStatus().name()));
      document.add(new Paragraph(" "));

      // 3. Product Table
      PdfPTable table = new PdfPTable(4);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{4f, 1f, 2f, 2f});

      // Table headers
      String[] headers = {"Producto", "Cant.", "Precio Unit.", "Subtotal"};
      for (String header : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
      }

      // Product rows
      for (InvoiceItem item : invoice.getItems()) {
        table.addCell(item.getStockEntry().getProduct().getName());
        table.addCell(String.valueOf(item.getQuantity()));
        table.addCell("$" + item.getUnitPrice().toString());
        table.addCell("$" + item.getSubTotal().toString());
      }
      document.add(table);
      document.add(new Paragraph(" "));

      // 4. Totals
      Paragraph totals = new Paragraph();
      totals.setAlignment(Element.ALIGN_RIGHT);
      totals.add("Subtotal: $" + invoice.getTotalGross() + "\n");
      totals.add("Descuentos: $" + invoice.getTotalDiscount() + "\n");
      totals.add(new Chunk("Total a Pagar: $" + invoice.getNetAmount() + "\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
      totals.add("Saldo Pendiente: $" + invoice.getBalanceDue());
      document.add(totals);

      // 5. Payment History
      if (invoice.getPayments() != null && !invoice.getPayments().isEmpty()) {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Paragraph paymentTitle = new Paragraph("Historial de Pagos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        paymentTitle.setSpacingAfter(10);
        document.add(paymentTitle);

        PdfPTable paymentTable = new PdfPTable(4);
        paymentTable.setWidthPercentage(100);
        paymentTable.setWidths(new float[]{3f, 2f, 3f, 2f});

        // Pay table headings
        String[] paymentHeaders = {"Fecha", "Método", "Referencia", "Monto"};
        for (String header : paymentHeaders) {
          PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
          cell.setBackgroundColor(Color.LIGHT_GRAY);
          cell.setPadding(5);
          paymentTable.addCell(cell);
        }

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (com.portfolio.inventory.domain.model.Payment payment : invoice.getPayments()) {
          paymentTable.addCell(payment.getPaymentDate().format(formatter));
          paymentTable.addCell(payment.getMethod().name());

          String ref = (payment.getBankReference() != null && !payment.getBankReference().isBlank())
            ? payment.getBankReference()
            : "N/A";
          paymentTable.addCell(ref);

          paymentTable.addCell("$" + payment.getAmount().toString());
        }
        document.add(paymentTable);
      }

      document.close();
      return baos.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Error generating the invoice PDF", e);
    }
  }
}