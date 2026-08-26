package com.portfolio.inventory.infrastructure.out.database;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(columnDefinition = "TEXT") // Usamos TEXT por si "detalles" es un texto muy largo
  private String details;

  // Aquí está la magia del "Link" (Foreign Key)
  // FetchType.LAZY hace que la categoría no se cargue de la BD hasta que no la pidas explícitamente, mejorando el rendimiento.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;
}