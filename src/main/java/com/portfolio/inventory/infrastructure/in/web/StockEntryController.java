package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.application.StockEntryService;
import com.portfolio.inventory.domain.model.StockEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/stock-entries")
@RequiredArgsConstructor
@Tag(name = "Stock Entries", description = "Gestión de Entradas de Inventario y Facturación")
public class StockEntryController {

  private final StockEntryService stockEntryService;

  @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Registrar una nueva entrada de inventario con comprobante opcional")
  public ResponseEntity<StockEntry> create(
    // Separamos la petición en dos partes: el DTO (JSON) y el archivo binario
    @RequestPart("data") @Valid StockEntryRequest request,
    @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    StockEntry entry = StockEntry.builder()
      .initialStock(request.initialStock())
      .currentStock(request.currentStock())
      .purchasePrice(request.purchasePrice())
      .salePrice(request.salePrice())
      .enteredBy(request.enteredBy())
      // El receiptUrl ya no viene del request de texto, lo genera el servicio
      .build();

    return new ResponseEntity<>(
      stockEntryService.createStockEntry(entry, request.productId(), request.supplierId(), file),
      HttpStatus.CREATED
    );
  }

  @GetMapping
  @Operation(summary = "Obtener todo el historial de entradas")
  public ResponseEntity<Page<StockEntry>> getAll(
    @PageableDefault(size = 10, page = 0, sort = "entryDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(stockEntryService.getAll(pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener una entrada específica por ID")
  public ResponseEntity<StockEntry> getById(@PathVariable Long id) {
    return ResponseEntity.ok(stockEntryService.getById(id));
  }

  @GetMapping("/product/{productId}")
  @Operation(summary = "Obtener historial de entradas de un producto")
  public ResponseEntity<Page<StockEntry>> getByProduct(
    @PathVariable Long productId,
    @PageableDefault(size = 10, page = 0, sort = "entryDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(stockEntryService.getByProductId(productId, pageable));
  }

  @GetMapping("/supplier/{supplierId}")
  @Operation(summary = "Obtener historial de facturación de un proveedor")
  public ResponseEntity<Page<StockEntry>> getBySupplier(
    @PathVariable Long supplierId,
    @PageableDefault(size = 10, page = 0, sort = "entryDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(stockEntryService.getBySupplierId(supplierId, pageable));
  }
}