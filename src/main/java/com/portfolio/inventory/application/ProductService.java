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
      throw new IllegalArgumentException("The product already exists in the inventory.");
    }

    Category category = categoryRepository.findById(categoryId)
      .orElseThrow(() -> new ResourceNotFoundException("Cannot create the product: The category with ID " + categoryId + " does not exist"));

    product.setCategory(category);

    return productRepository.save(product);
  }

  public Product updateProduct(Long id, Product updatedProduct, Long categoryId) {
    Product existingProduct = getProductById(id);

    Category category = categoryRepository.findById(categoryId)
      .orElseThrow(() -> new ResourceNotFoundException("Cannot update: The category with ID " + categoryId + " does not exist"));

    if (!existingProduct.getName().equalsIgnoreCase(updatedProduct.getName()) && productRepository.existsByName(updatedProduct.getName())) {
      throw new IllegalArgumentException("Another product with the name already exists: " + updatedProduct.getName());
    }

    existingProduct.setName(updatedProduct.getName());
    existingProduct.setDetails(updatedProduct.getDetails());
    existingProduct.setCategory(category);

    return productRepository.save(existingProduct);
  }

  public Page<Product> getAllProducts(String search, Long categoryId, Pageable pageable) {
    String finalSearch = (search != null && !search.trim().isEmpty()) ? search : "";
    return productRepository.searchAndFilterProducts(finalSearch, categoryId, pageable);
  }

  public Product getProductById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
  }

  public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
    if (categoryRepository.findById(categoryId).isEmpty()) {
      throw new ResourceNotFoundException("The category with ID " + categoryId + " does not exist");
    }
    return productRepository.findByCategoryId(categoryId, pageable);
  }

  public void deleteProduct(Long id) {
    Product product = getProductById(id);
    productRepository.deleteById(product.getId());
  }
}