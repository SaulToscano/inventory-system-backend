package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.Category;
import com.portfolio.inventory.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Habilita el uso de anotaciones de Mockito (@Mock, @InjectMocks)
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryService categoryService;

  private Category testCategory;

  @BeforeEach
  void setUp() {
    // Se ejecuta antes de cada prueba para tener datos limpios
    testCategory = Category.builder()
      .id(1L)
      .name("Electrónica")
      .description("Dispositivos electrónicos")
      .build();
  }

  @Test
  void givenValidCategory_whenCreateCategory_thenReturnsSavedCategory() {
    // Given (Dado que el nombre no existe y el repositorio guardará la categoría)
    when(categoryRepository.existsByName(anyString())).thenReturn(false);
    when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

    // When (Cuando llamamos al método del servicio)
    Category result = categoryService.createCategory(testCategory);

    // Then (Entonces verificamos que el resultado es el esperado)
    assertNotNull(result);
    assertEquals("Electrónica", result.getName());

    // Verificamos que el repositorio fue llamado exactamente una vez
    verify(categoryRepository, times(1)).existsByName(anyString());
    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  @Test
  void givenExistingName_whenCreateCategory_thenThrowsIllegalArgumentException() {
    // Given (Dado que el nombre YA existe en la base de datos)
    when(categoryRepository.existsByName("Electrónica")).thenReturn(true);

    // When & Then (Cuando intentamos crear, Entonces esperamos una excepción)
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> categoryService.createCategory(testCategory)
    );

    assertEquals("La categoría ya existe", exception.getMessage());

    // Verificamos que el método save NUNCA se llamó
    verify(categoryRepository, never()).save(any(Category.class));
  }

  @Test
  void givenExistingId_whenGetCategoryById_thenReturnsCategory() {
    // Given
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

    // When
    Category result = categoryService.getCategoryById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(categoryRepository, times(1)).findById(1L);
  }

  @Test
  void givenNonExistingId_whenGetCategoryById_thenThrowsResourceNotFoundException() {
    // Given (La base de datos devuelve un Optional vacío)
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception = assertThrows(
      ResourceNotFoundException.class,
      () -> categoryService.getCategoryById(99L)
    );

    assertTrue(exception.getMessage().contains("99"));
    verify(categoryRepository, times(1)).findById(99L);
  }
}