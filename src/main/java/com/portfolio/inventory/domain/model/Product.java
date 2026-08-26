package com.portfolio.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
  private Long id;
  private String name;
  private String details;

  // Aquí hacemos el "link" en la capa de negocio.
  // Un Producto TIENE una Categoría.
  private Category category;
}