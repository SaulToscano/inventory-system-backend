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
    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new ResourceNotFoundException("Cannot register the entry: The product with ID " + productId + " does not exist"));

    Supplier supplier = supplierRepository.findById(supplierId)
      .orElseThrow(() -> new ResourceNotFoundException("Cannot register the entry: The supplier with ID " + supplierId + " does not exist"));

    // File Upload Logic
    if (file != null && !file.isEmpty()) {
      try {
        String fileUrl = fileStoragePort.uploadFile(
          file.getOriginalFilename(),
          file.getBytes(),
          file.getContentType()
        );
        stockEntry.setReceiptUrl(fileUrl);
      } catch (IOException e) {
        throw new RuntimeException("Error processing the voucher file", e);
      }
    }

    stockEntry.setProduct(product);
    stockEntry.setSupplier(supplier);
    stockEntry.setEntryDate(LocalDateTime.now());

    return stockEntryRepository.save(stockEntry);
  }

  public Page<StockEntry> getAll(String search, Long productId, Long supplierId, Pageable pageable) {
    String finalSearch = (search != null && !search.trim().isEmpty()) ? search : "";
    return stockEntryRepository.searchAndFilterStockEntries(finalSearch, productId, supplierId, pageable);
  }

  public StockEntry getById(Long id) {
    return stockEntryRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found with ID: " + id));
  }

  public Page<StockEntry> getByProductId(Long productId, Pageable pageable) {
    if (productRepository.findById(productId).isEmpty()) {
      throw new ResourceNotFoundException("The product with ID " + productId + " does not exist");
    }
    return stockEntryRepository.findByProductId(productId, pageable);
  }

  public Page<StockEntry> getBySupplierId(Long supplierId, Pageable pageable) {
    if (supplierRepository.findById(supplierId).isEmpty()) {
      throw new ResourceNotFoundException("The supplier with ID " + supplierId + " does not exist");
    }
    return stockEntryRepository.findBySupplierId(supplierId, pageable);
  }
}