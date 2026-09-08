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
  private final CategoryRepository categoryRepository;

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

  // === NUEVO MÉTODO PARA ACTUALIZAR ===
  public Product updateProduct(Long id, Product updatedProduct, Long categoryId) {
    // 1. Buscamos el producto existente (reutilizamos tu propio método)
    Product existingProduct = getProductById(id);

    // 2. Validamos que la nueva categoría exista
    Category category = categoryRepository.findById(categoryId)
      .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: La categoría con ID " + categoryId + " no existe"));

    // 3. Verificamos si el nombre cambió y si el nuevo nombre ya está ocupado por otro producto
    if (!existingProduct.getName().equalsIgnoreCase(updatedProduct.getName()) && productRepository.existsByName(updatedProduct.getName())) {
      throw new IllegalArgumentException("Ya existe otro producto con el nombre: " + updatedProduct.getName());
    }

    // 4. Actualizamos los campos
    existingProduct.setName(updatedProduct.getName());
    existingProduct.setDetails(updatedProduct.getDetails());
    existingProduct.setCategory(category);

    // 5. Guardamos y retornamos
    return productRepository.save(existingProduct);
  }

  public Page<Product> getAllProducts(String search, Long categoryId, Pageable pageable) {
    String finalSearch = (search != null && !search.trim().isEmpty()) ? search : "";
    return productRepository.searchAndFilterProducts(finalSearch, categoryId, pageable);
  }

  public Product getProductById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
  }

  public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
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