package com.portfolio.inventory.infrastructure.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.inventory.application.CategoryService;
import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest carga solo la capa web (Controladores, ControllerAdvice, Validaciones)
@WebMvcTest(CategoryController.class)
// Apagamos los filtros de seguridad para probar el controlador de forma aislada
@AutoConfigureMockMvc(addFilters = false)
@Import(ObjectMapper.class)
class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc; // Simula las peticiones HTTP (Postman/Insomnia de código)

  @Autowired
  private ObjectMapper objectMapper; // Convierte objetos Java a JSON y viceversa

  @MockitoBean
  private CategoryService categoryService; // Simulamos el servicio para no tocar la BD

  private Category testCategory;
  private CategoryRequest validRequest;

  @BeforeEach
  void setUp() {
    testCategory = Category.builder()
      .id(1L)
      .name("Electrónica")
      .description("Dispositivos")
      .build();

    validRequest = new CategoryRequest("Electrónica", "Dispositivos");
  }

  @Test
  void givenValidRequest_whenCreate_thenReturns201Created() throws Exception {
    when(categoryService.createCategory(any(Category.class))).thenReturn(testCategory);

    mockMvc.perform(post("/api/v1/categories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validRequest)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value("Electrónica"));
  }

  @Test
  void givenInvalidRequest_whenCreate_thenReturns400BadRequest() throws Exception {
    // Petición inválida: nombre vacío (rompe la regla @NotBlank)
    CategoryRequest invalidRequest = new CategoryRequest("", "Sin nombre");

    mockMvc.perform(post("/api/v1/categories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
      // Verificamos que el GlobalExceptionHandler atrapó el error
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Error en la validación de los datos enviados"))
      .andExpect(jsonPath("$.details[0]").value("name: El nombre es obligatorio"));
  }

  @Test
  void givenCategories_whenGetAll_thenReturns200AndPage() throws Exception {
    when(categoryService.getAllCategories(any(Pageable.class)))
      .thenReturn(new PageImpl<>(List.of(testCategory)));

    mockMvc.perform(get("/api/v1/categories"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content[0].name").value("Electrónica"))
      .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void givenExistingId_whenGetById_thenReturns200() throws Exception {
    when(categoryService.getCategoryById(1L)).thenReturn(testCategory);

    mockMvc.perform(get("/api/v1/categories/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Electrónica"));
  }

  @Test
  void givenNonExistingId_whenGetById_thenReturns404() throws Exception {
    when(categoryService.getCategoryById(99L))
      .thenThrow(new ResourceNotFoundException("La categoría con ID 99 no fue encontrada"));

    mockMvc.perform(get("/api/v1/categories/{id}", 99L))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Not Found"))
      .andExpect(jsonPath("$.message").value("La categoría con ID 99 no fue encontrada"));
  }
}