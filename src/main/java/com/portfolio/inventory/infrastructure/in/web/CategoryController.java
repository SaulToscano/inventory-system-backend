package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.application.CategoryService;
import com.portfolio.inventory.domain.model.Category;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "CRUD de Categorías")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping
  @Operation(summary = "Crear una nueva categoría")
  public ResponseEntity<Category> create(@Valid @RequestBody CategoryRequest request) {
    Category category = Category.builder()
      .name(request.name())
      .description(request.description())
      .build();
    return new ResponseEntity<>(categoryService.createCategory(category), HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(summary = "Obtener todas las categorías paginadas")
  public ResponseEntity<Page<Category>> getAll(
    @PageableDefault(size = 10, page = 0) Pageable pageable) {
    return ResponseEntity.ok(categoryService.getAllCategories(pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener una categoría por ID")
  public ResponseEntity<Category> getById(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.getCategoryById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar una categoría")
  public ResponseEntity<Category> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
    Category category = Category.builder()
      .name(request.name())
      .description(request.description())
      .build();
    return ResponseEntity.ok(categoryService.updateCategory(id, category));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Eliminar una categoría")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}