package com.portfolio.inventory.application;

import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import com.portfolio.inventory.domain.model.Customer;
import com.portfolio.inventory.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  public Customer createCustomer(Customer customer) {
    if (customerRepository.existsByEmail(customer.getEmail())) {
      throw new IllegalArgumentException("Ya existe un cliente registrado con ese correo electrónico");
    }
    return customerRepository.save(customer);
  }

  public Page<Customer> getAllCustomers(Pageable pageable) {
    return customerRepository.findAll(pageable);
  }

  public Customer getCustomerById(Long id) {
    return customerRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el ID: " + id));
  }

  public Customer updateCustomer(Long id, Customer customerUpdate) {
    Customer existingCustomer = getCustomerById(id);

    // Verificamos que si cambia el correo, no choque con otro existente
    if (!existingCustomer.getEmail().equals(customerUpdate.getEmail()) &&
      customerRepository.existsByEmail(customerUpdate.getEmail())) {
      throw new IllegalArgumentException("El nuevo correo electrónico ya está en uso por otro cliente");
    }

    existingCustomer.setName(customerUpdate.getName());
    existingCustomer.setEmail(customerUpdate.getEmail());
    existingCustomer.setAddress(customerUpdate.getAddress());
    existingCustomer.setPhone(customerUpdate.getPhone());

    return customerRepository.save(existingCustomer);
  }

  public void deleteCustomer(Long id) {
    Customer customer = getCustomerById(id);
    customerRepository.deleteById(customer.getId());
  }
}