package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.application.InvoicePdfService;
import com.portfolio.inventory.application.InvoiceService;
import com.portfolio.inventory.domain.model.Invoice;
import com.portfolio.inventory.domain.model.InvoiceItem;
import com.portfolio.inventory.domain.model.Payment;
import com.portfolio.inventory.domain.model.StockEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Facturación y Salidas de Inventario")
public class InvoiceController {

  private final InvoiceService invoiceService;
  private final InvoicePdfService invoicePdfService;

  @GetMapping
  @Operation(summary = "Obtener el listado general de facturas (Paginado)")
  public ResponseEntity<org.springframework.data.domain.Page<Invoice>> getAll(
    @PageableDefault(size = 10, page = 0, sort = "issueDate", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {

    return ResponseEntity.ok(invoiceService.getAllInvoices(pageable));
  }

  @PostMapping
  @Operation(summary = "Generar una nueva factura y descontar stock por lotes")
  public ResponseEntity<Invoice> create(@Valid @RequestBody InvoiceRequest request) {

    // 1. Convertir el DTO de los items al Dominio
    List<InvoiceItem> items = request.items().stream().map(dto ->
      InvoiceItem.builder()
        .stockEntry(StockEntry.builder().id(dto.stockEntryId()).build()) // Pasamos el ID del lote
        .quantity(dto.quantity())
        .unitPrice(dto.unitPrice())
        .discount(dto.discount())
        .discountType(dto.discountType())
        .build()
    ).toList();

    // 2. Convertir el pago inicial (si existe)
    Payment initialPayment = null;
    if (request.initialPayment() != null) {
      initialPayment = Payment.builder()
        .amount(request.initialPayment().amount())
        .method(request.initialPayment().method())
        .bankReference(request.initialPayment().bankReference())
        .build();
    }

    // 3. Llamar al servicio
    Invoice generatedInvoice = invoiceService.generateInvoice(request.customerId(), items, initialPayment);

    return new ResponseEntity<>(generatedInvoice, HttpStatus.CREATED);
  }

  @PostMapping("/{id}/payments")
  @Operation(summary = "Registrar un abono a una factura existente")
  public ResponseEntity<Invoice> addPayment(
    @PathVariable Long id,
    @Valid @RequestBody PaymentRequest request) {

    return ResponseEntity.ok(invoiceService.registerPayment(id, request));
  }

  @GetMapping(value = "/{id}/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
  @Operation(summary = "Descargar factura en formato PDF")
  public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {

    byte[] pdfBytes = invoicePdfService.generateInvoicePdf(id);

    return ResponseEntity.ok()
      // Esto le indica al navegador que es un archivo adjunto que debe descargar
      .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura_" + id + ".pdf")
      .body(pdfBytes);
  }
}