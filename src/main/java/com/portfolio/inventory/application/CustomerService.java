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
      throw new IllegalArgumentException("A customer with that email address is already registered.");
    }
    return customerRepository.save(customer);
  }

  public Page<Customer> getAllCustomers(String search, Pageable pageable) {
    String finalSearch = (search != null && !search.trim().isEmpty()) ? search : "";
    return customerRepository.searchCustomers(finalSearch, pageable);
  }

  public Customer getCustomerById(Long id) {
    return customerRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
  }

  public Customer updateCustomer(Long id, Customer customerUpdate) {
    Customer existingCustomer = getCustomerById(id);

    if (!existingCustomer.getEmail().equals(customerUpdate.getEmail()) &&
      customerRepository.existsByEmail(customerUpdate.getEmail())) {
      throw new IllegalArgumentException("The new email address is already in use by another customer.");
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