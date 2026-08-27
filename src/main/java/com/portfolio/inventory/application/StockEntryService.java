package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.Product;
import com.portfolio.inventory.domain.model.StockEntry;
import com.portfolio.inventory.domain.model.Supplier;
import com.portfolio.inventory.domain.repository.FileStoragePort;
import com.portfolio.inventory.domain.repository.ProductRepository;
import com.portfolio.inventory.domain.repository.StockEntryRepository;
import com.portfolio.inventory.domain.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockEntryService {

  private final StockEntryRepository stockEntryRepository;
  private final ProductRepository productRepository;
  private final SupplierRepository supplierRepository;
  private final FileStoragePort fileStoragePort;

  public StockEntry createStockEntry(StockEntry stockEntry, Long productId, Long supplierId, MultipartFile file) {
    // 1. Validar que el Producto exista
    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new ResourceNotFoundException("No se puede registrar la entrada: El producto con ID " + productId + " no existe"));

    // 2. Validar que el Proveedor exista
    Supplier supplier = supplierRepository.findById(supplierId)
      .orElseThrow(() -> new ResourceNotFoundException("No se puede registrar la entrada: El proveedor con ID " + supplierId + " no existe"));

    // LÓGICA DE SUBIDA DE ARCHIVO
    if (file != null && !file.isEmpty()) {
      try {
        String fileUrl = fileStoragePort.uploadFile(
          file.getOriginalFilename(),
          file.getBytes(),
          file.getContentType()
        );
        stockEntry.setReceiptUrl(fileUrl);
      } catch (IOException e) {
        throw new RuntimeException("Error al procesar el archivo del comprobante", e);
      }
    }

    // 3. Vincular relaciones y establecer la fecha de auditoría automática
    stockEntry.setProduct(product);
    stockEntry.setSupplier(supplier);
    stockEntry.setEntryDate(LocalDateTime.now());

    // 4. Guardar el registro
    return stockEntryRepository.save(stockEntry);
  }

  public Page<StockEntry> getAll(Pageable pageable) {
    return stockEntryRepository.findAll(pageable);
  }

  public StockEntry getById(Long id) {
    return stockEntryRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Registro de inventario no encontrado con el ID: " + id));
  }

  public Page<StockEntry> getByProductId(Long productId, Pageable pageable) {
    if (productRepository.findById(productId).isEmpty()) {
      throw new ResourceNotFoundException("El producto con ID " + productId + " no existe");
    }
    return stockEntryRepository.findByProductId(productId, pageable);
  }

  public Page<StockEntry> getBySupplierId(Long supplierId, Pageable pageable) {
    if (supplierRepository.findById(supplierId).isEmpty()) {
      throw new ResourceNotFoundException("El proveedor con ID " + supplierId + " no existe");
    }
    return stockEntryRepository.findBySupplierId(supplierId, pageable);
  }

  // Nota: Por reglas de auditoría contable, usualmente las entradas de stock NO se eliminan ni modifican.
  // Si hay un error, se hace un movimiento de compensación (salida).
  // Por simplicidad del portafolio, dejaremos fuera el update/delete, o puedes agregarlos si lo prefieres.
}