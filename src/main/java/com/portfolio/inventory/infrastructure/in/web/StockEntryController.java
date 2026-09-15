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
@Tag(name = "Stock Entries", description = "Inventory Receipt and Invoicing Management")
public class StockEntryController {
  private final StockEntryService stockEntryService;

  @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Record a new inventory entry with an optional receipt")
  public ResponseEntity<StockEntry> create(
    @RequestPart("data") @Valid StockEntryRequest request,
    @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    StockEntry entry = StockEntry.builder()
      .initialStock(request.initialStock())
      .currentStock(request.currentStock())
      .purchasePrice(request.purchasePrice())
      .salePrice(request.salePrice())
      .enteredBy(request.enteredBy())
      .build();

    return new ResponseEntity<>(
      stockEntryService.createStockEntry(entry, request.productId(), request.supplierId(), file),
      HttpStatus.CREATED
    );
  }

  @GetMapping
  @Operation(summary = "Retrieve the full history of entries, filtered and paginated.")
  public ResponseEntity<Page<StockEntry>> getAll(
    @RequestParam(required = false) String search,
    @RequestParam(required = false) Long productId,
    @RequestParam(required = false) Long supplierId,
    @PageableDefault(size = 10, page = 0, sort = "entryDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

    return ResponseEntity.ok(stockEntryService.getAll(search, productId, supplierId, pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Retrieve a specific entry by ID")
  public ResponseEntity<StockEntry> getById(@PathVariable Long id) {
    return ResponseEntity.ok(stockEntryService.getById(id));
  }

  @GetMapping("/product/{productId}")
  @Operation(summary = "Retrieve product entry history")
  public ResponseEntity<Page<StockEntry>> getByProduct(
    @PathVariable Long productId,
    @PageableDefault(size = 10, page = 0, sort = "entryDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(stockEntryService.getByProductId(productId, pageable));
  }

  @GetMapping("/supplier/{supplierId}")
  @Operation(summary = "Retrieve a supplier's billing history")
  public ResponseEntity<Page<StockEntry>> getBySupplier(
    @PathVariable Long supplierId,
    @PageableDefault(size = 10, page = 0, sort = "entryDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(stockEntryService.getBySupplierId(supplierId, pageable));
  }
}