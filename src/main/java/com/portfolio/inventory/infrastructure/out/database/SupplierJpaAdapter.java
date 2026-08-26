package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.Supplier;
import com.portfolio.inventory.domain.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SupplierJpaAdapter implements SupplierRepository {

  private final SpringDataSupplierRepository repository;

  @Override
  public Supplier save(Supplier supplier) {
    SupplierEntity entity = SupplierEntity.builder()
      .id(supplier.getId())
      .name(supplier.getName())
      .email(supplier.getEmail())
      .phone(supplier.getPhone())
      .address(supplier.getAddress())
      .build();

    SupplierEntity savedEntity = repository.save(entity);
    return toDomain(savedEntity);
  }

  @Override
  public Optional<Supplier> findById(Long id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public Page<Supplier> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(this::toDomain);
  }

  @Override
  public void deleteById(Long id) {
    repository.deleteById(id);
  }

  @Override
  public boolean existsByName(String name) {
    return repository.existsByName(name);
  }

  private Supplier toDomain(SupplierEntity entity) {
    return Supplier.builder()
      .id(entity.getId())
      .name(entity.getName())
      .email(entity.getEmail())
      .phone(entity.getPhone())
      .address(entity.getAddress())
      .build();
  }
}