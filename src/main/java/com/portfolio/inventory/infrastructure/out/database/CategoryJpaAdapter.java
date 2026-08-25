package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.Category;
import com.portfolio.inventory.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryJpaAdapter implements CategoryRepository {

  private final SpringDataCategoryRepository repository;

  @Override
  public Category save(Category category) {
    CategoryEntity entity = CategoryEntity.builder()
      .id(category.getId())
      .name(category.getName())
      .description(category.getDescription())
      .build();
    CategoryEntity savedEntity = repository.save(entity);
    return toDomain(savedEntity);
  }

  @Override
  public Optional<Category> findById(Long id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public Page<Category> findAll(Pageable pageable) {
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

  // Método auxiliar para mapear de Entidad a Dominio
  private Category toDomain(CategoryEntity entity) {
    return Category.builder()
      .id(entity.getId())
      .name(entity.getName())
      .description(entity.getDescription())
      .build();
  }
}