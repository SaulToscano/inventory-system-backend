package com.portfolio.inventory.infrastructure.in.web;

import com.portfolio.inventory.application.CustomerService;
import com.portfolio.inventory.domain.model.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer Management")
public class CustomerController {
  private final CustomerService customerService;

  @PostMapping
  @Operation(summary = "Register a new client")
  public ResponseEntity<Customer> create(@Valid @RequestBody CustomerRequest request) {
    Customer customer = Customer.builder()
      .name(request.name())
      .email(request.email())
      .address(request.address())
      .phone(request.phone())
      .build();
    return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
  }

  @GetMapping
  @Operation(summary = "Retrieve a paginated and filtered list of customers")
  public ResponseEntity<Page<Customer>> getAll(
    @RequestParam(required = false) String search,
    @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable) {
    return ResponseEntity.ok(customerService.getAllCustomers(search, pageable));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get details for a specific customer")
  public ResponseEntity<Customer> getById(@PathVariable Long id) {
    return ResponseEntity.ok(customerService.getCustomerById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a client's information")
  public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
    Customer customer = Customer.builder()
      .name(request.name())
      .email(request.email())
      .address(request.address())
      .phone(request.phone())
      .build();
    return ResponseEntity.ok(customerService.updateCustomer(id, customer));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a client")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }
}