package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.model.Category;
import com.portfolio.inventory.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.portfolio.inventory.domain.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Category createCategory(Category category) {
    if (categoryRepository.existsByName(category.getName())) {
      throw new IllegalArgumentException("La categoría ya existe");
    }
    return categoryRepository.save(category);
  }

  public Page<Category> getAllCategories(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + id + " no fue encontrada"));
  }

  public Category updateCategory(Long id, Category categoryUpdate) {
    Category existingCategory = getCategoryById(id);
    existingCategory.setName(categoryUpdate.getName());
    existingCategory.setDescription(categoryUpdate.getDescription());
    return categoryRepository.save(existingCategory);
  }

  public void deleteCategory(Long id) {
    Category category = getCategoryById(id);
    categoryRepository.deleteById(category.getId());
  }
}