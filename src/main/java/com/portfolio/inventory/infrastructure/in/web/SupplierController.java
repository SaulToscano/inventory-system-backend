package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.application.SupplierService;
import com.portfolio.inventory.domain.model.Supplier;
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

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "CRUD de Proveedores")
public class SupplierController {

  private final SupplierService supplierService;

  @PostMapping
  @Operation(summary = "Register a new supplier")
  public ResponseEntity<Supplier> create(@Valid @RequestBody SupplierRequest request) {
    Supplier supplier = Supplier.builder()
      .name(request.name())
      .email(request.email())
      .phone(request.phone())
      .address(request.address())
      .build();
    return new ResponseEntity<>(supplierService.createSupplier(supplier), HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(summary = "Retrieve all suppliers, paginated and filtered.")
  public ResponseEntity<Page<Supplier>> getAll(
    @RequestParam(required = false) String search,
    @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable) {
    return ResponseEntity.ok(supplierService.getAllSuppliers(search, pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a provider by ID")
  public ResponseEntity<Supplier> getById(@PathVariable Long id) {
    return ResponseEntity.ok(supplierService.getSupplierById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a supplier's details")
  public ResponseEntity<Supplier> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
    Supplier supplier = Supplier.builder()
      .name(request.name())
      .email(request.email())
      .phone(request.phone())
      .address(request.address())
      .build();
    return ResponseEntity.ok(supplierService.updateSupplier(id, supplier));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Remove a supplier from the catalog")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    supplierService.deleteSupplier(id);
    return ResponseEntity.noContent().build();
  }
}