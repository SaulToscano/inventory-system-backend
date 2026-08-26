package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.Category;
import com.portfolio.inventory.domain.model.Product;
import com.portfolio.inventory.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductJpaAdapter implements ProductRepository {

  private final SpringDataProductRepository repository;

  @Override
  public Product save(Product product) {
    // 1. Mapeamos la Categoría (Hibernate solo necesita el ID para hacer la relación)
    CategoryEntity categoryEntity = CategoryEntity.builder()
      .id(product.getCategory().getId())
      .name(product.getCategory().getName())
      .build();

    // 2. Mapeamos el Producto
    ProductEntity entity = ProductEntity.builder()
      .id(product.getId())
      .name(product.getName())
      .details(product.getDetails())
      .category(categoryEntity)
      .build();

    ProductEntity savedEntity = repository.save(entity);
    return toDomain(savedEntity);
  }

  @Override
  public Optional<Product> findById(Long id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public Page<Product> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(this::toDomain);
  }

  @Override
  public void deleteById(Long id) {
    repository.deleteById(id);
  }

  @Override
  public boolean existsByName(String name) {
    return repository.existsByName(name);
  }

  @Override
  public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
    return repository.findByCategoryId(categoryId, pageable).map(this::toDomain);
  }

  // Método auxiliar para transformar Entidades JPA en Objetos de Dominio puros
  private Product toDomain(ProductEntity entity) {
    Category category = Category.builder()
      .id(entity.getCategory().getId())
      .name(entity.getCategory().getName())
      .description(entity.getCategory().getDescription())
      .build();

    return Product.builder()
      .id(entity.getId())
      .name(entity.getName())
      .details(entity.getDetails())
      .category(category)
      .build();
  }
}