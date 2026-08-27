package com.portfolio.inventory.infrastructure.out.database;

import com.portfolio.inventory.domain.model.Customer;
import com.portfolio.inventory.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerJpaAdapter implements CustomerRepository {

  private final SpringDataCustomerRepository repository;

  @Override
  public Customer save(Customer customer) {
    CustomerEntity entity = CustomerEntity.builder()
      .id(customer.getId())
      .name(customer.getName())
      .email(customer.getEmail())
      .address(customer.getAddress())
      .phone(customer.getPhone())
      .build();

    CustomerEntity savedEntity = repository.save(entity);
    return toDomain(savedEntity);
  }

  @Override
  public Optional<Customer> findById(Long id) {
    return repository.findById(id).map(this::toDomain);
  }

  @Override
  public Page<Customer> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(this::toDomain);
  }

  @Override
  public void deleteById(Long id) {
    repository.deleteById(id);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  private Customer toDomain(CustomerEntity entity) {
    return Customer.builder()
      .id(entity.getId())
      .name(entity.getName())
      .email(entity.getEmail())
      .address(entity.getAddress())
      .phone(entity.getPhone())
      .build();
  }
}