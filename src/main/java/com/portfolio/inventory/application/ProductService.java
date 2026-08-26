package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.Category;
import com.portfolio.inventory.domain.model.Product;
import com.portfolio.inventory.domain.repository.CategoryRepository;
import com.portfolio.inventory.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository; // Lo necesitamos para validar la llave foránea

  public Product createProduct(Product product, Long categoryId) {
    if (productRepository.existsByName(product.getName())) {
      throw new IllegalArgumentException("El producto ya existe en el inventario");
    }

    // 1. Buscamos que la categoría realmente exista
    Category category = categoryRepository.findById(categoryId)
      .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el producto: La categoría con ID " + categoryId + " no existe"));

    // 2. Vinculamos la categoría al producto
    product.setCategory(category);

    // 3. Guardamos
    return productRepository.save(product);
  }

  public Page<Product> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

  public Product getProductById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
  }

  public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
    // Validamos que la categoría exista antes de buscar sus productos
    if (categoryRepository.findById(categoryId).isEmpty()) {
      throw new ResourceNotFoundException("La categoría con ID " + categoryId + " no existe");
    }
    return productRepository.findByCategoryId(categoryId, pageable);
  }

  public void deleteProduct(Long id) {
    Product product = getProductById(id);
    productRepository.deleteById(product.getId());
  }
}