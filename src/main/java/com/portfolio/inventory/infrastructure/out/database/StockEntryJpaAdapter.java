package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.Product;
import com.portfolio.inventory.domain.model.StockEntry;
import com.portfolio.inventory.domain.model.Supplier;
import com.portfolio.inventory.domain.repository.StockEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockEntryJpaAdapter implements StockEntryRepository {

  private final SpringDataStockEntryRepository repository;

  @Override
  public StockEntry save(StockEntry entry) {
    ProductEntity productEntity = ProductEntity.builder()
      .id(entry.getProduct().getId())
      .build();

    SupplierEntity supplierEntity = SupplierEntity.builder()
      .id(entry.getSupplier().getId())
      .build();

    StockEntryEntity entity = StockEntryEntity.builder()
      .id(entry.getId())
      .product(productEntity)
      .supplier(supplierEntity)
      .receiptUrl(entry.getReceiptUrl())
      .initialStock(entry.getInitialStock())
      .currentStock(entry.getCurrentStock())
      .purchasePrice(entry.getPurchasePrice())
      .salePrice(entry.getSalePrice())
      .enteredBy(entry.getEnteredBy())
      .entryDate(entry.getEntryDate())
      .build();

    // 1. Guardamos en la base de datos
    StockEntryEntity savedEntity = repository.save(entity);

    // 2. ¡EL TRUCO! En lugar de llamar a toDomain(savedEntity) que lanza el error de Proxy,
    // simplemente le ponemos el nuevo ID de la BD a nuestro objeto original y lo devolvemos intacto.
    entry.setId(savedEntity.getId());
    return entry;
  }

  @Override
  public Optional<StockEntry> findById(Long id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public Page<StockEntry> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(this::toDomain);
  }

  @Override
  public Page<StockEntry> findByProductId(Long productId, Pageable pageable) {
    return repository.findByProductId(productId, pageable).map(this::toDomain);
  }

  @Override
  public Page<StockEntry> findBySupplierId(Long supplierId, Pageable pageable) {
    return repository.findBySupplierId(supplierId, pageable).map(this::toDomain);
  }

  private StockEntry toDomain(StockEntryEntity entity) {
    // Mapeo básico para no saturar la memoria, solo traemos lo esencial
    Product product = Product.builder()
      .id(entity.getProduct().getId())
      .name(entity.getProduct().getName())
      .build();

    Supplier supplier = Supplier.builder()
      .id(entity.getSupplier().getId())
      .name(entity.getSupplier().getName())
      .build();

    return StockEntry.builder()
      .id(entity.getId())
      .product(product)
      .supplier(supplier)
      .receiptUrl(entity.getReceiptUrl())
      .initialStock(entity.getInitialStock())
      .currentStock(entity.getCurrentStock())
      .purchasePrice(entity.getPurchasePrice())
      .salePrice(entity.getSalePrice())
      .enteredBy(entity.getEnteredBy())
      .entryDate(entity.getEntryDate())
      .build();
  }
}