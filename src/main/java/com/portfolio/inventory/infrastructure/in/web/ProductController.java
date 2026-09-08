package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.application.ProductService;
import com.portfolio.inventory.domain.model.Product;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "CRUD de Productos del Inventario")
public class ProductController {

  private final ProductService productService;

  @PostMapping
  @Operation(summary = "Crear un nuevo producto vinculado a una categoría")
  public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request) {
    Product product = Product.builder()
      .name(request.name())
      .details(request.details())
      .build();

    // Le pasamos el producto y el ID de la categoría al servicio
    return new ResponseEntity<>(productService.createProduct(product, request.categoryId()), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar un producto")
  public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    Product product = Product.builder()
      .name(request.name())
      .details(request.details())
      .build();
    // Asegúrate de crear este método en tu ProductService
    return ResponseEntity.ok(productService.updateProduct(id, product, request.categoryId()));
  }

  @GetMapping
  @Operation(summary = "Obtener todos los productos paginados y filtrados")
  public ResponseEntity<Page<Product>> getAll(
    @RequestParam(required = false) String search,
    @RequestParam(required = false) Long categoryId,
    @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable) {

    return ResponseEntity.ok(productService.getAllProducts(search, categoryId, pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener un producto por ID")
  public ResponseEntity<Product> getById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @GetMapping("/category/{categoryId}")
  @Operation(summary = "Obtener todos los productos de una categoría específica")
  public ResponseEntity<Page<Product>> getByCategory(
    @PathVariable Long categoryId,
    @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable) {
    return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Eliminar un producto")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}