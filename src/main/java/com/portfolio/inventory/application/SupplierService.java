package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.Supplier;
import com.portfolio.inventory.domain.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierService {

  private final SupplierRepository supplierRepository;

  public Supplier createSupplier(Supplier supplier) {
    if (supplierRepository.existsByName(supplier.getName())) {
      throw new IllegalArgumentException("El proveedor ya existe en la base de datos");
    }
    return supplierRepository.save(supplier);
  }

  public Page<Supplier> getAllSuppliers(Pageable pageable) {
    return supplierRepository.findAll(pageable);
  }

  public Supplier getSupplierById(Long id) {
    return supplierRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con el ID: " + id));
  }

  public Supplier updateSupplier(Long id, Supplier supplierUpdate) {
    Supplier existingSupplier = getSupplierById(id);

    existingSupplier.setName(supplierUpdate.getName());
    existingSupplier.setEmail(supplierUpdate.getEmail());
    existingSupplier.setPhone(supplierUpdate.getPhone());
    existingSupplier.setAddress(supplierUpdate.getAddress());

    return supplierRepository.save(existingSupplier);
  }

  public void deleteSupplier(Long id) {
    Supplier supplier = getSupplierById(id);
    supplierRepository.deleteById(supplier.getId());
  }
}