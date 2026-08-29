package com.portfolio.inventory.infrastructure.config;

import com.portfolio.inventory.domain.model.*;
import com.portfolio.inventory.domain.model.enums.DiscountType;
import com.portfolio.inventory.domain.model.enums.InvoiceStatus;
import com.portfolio.inventory.domain.model.enums.PaymentMethod;
import com.portfolio.inventory.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

  // Inyectamos todos los repositorios de tu dominio
  private final CategoryRepository categoryRepository;
  private final SupplierRepository supplierRepository;
  private final CustomerRepository customerRepository;
  private final ProductRepository productRepository;
  private final StockEntryRepository stockEntryRepository;
  private final InvoiceRepository invoiceRepository;

  @Override
  public void run(String... args) throws Exception {
    // Candado: Si ya hay al menos 1 categoría, significa que la BD ya tiene datos y abortamos el Seed
    if (!categoryRepository.findAll(PageRequest.of(0, 1)).isEmpty()) {
      log.info("La base de datos ya contiene información. Se omite el Database Seeder.");
      return;
    }

    log.info("Iniciando el llenado de la base de datos con Mock Data...");

    // 1. Crear Categorías
    Category electronics = categoryRepository.save(Category.builder()
      .name("Electrónica")
      .description("Dispositivos y gadgets tecnológicos")
      .build());

    Category groceries = categoryRepository.save(Category.builder()
      .name("Abarrotes")
      .description("Productos de consumo diario")
      .build());

    // 2. Crear Proveedores
    Supplier techSupplier = supplierRepository.save(Supplier.builder()
      .name("TechCorp Global")
      .email("ventas@techcorp.com")
      .phone("555-102030")
      .address("Silicon Valley 123")
      .build());

    Supplier freshSupplier = supplierRepository.save(Supplier.builder()
      .name("Granjas del Norte")
      .email("contacto@granjas.com")
      .phone("555-908070")
      .address("Carretera Norte Km 45")
      .build());

    // 3. Crear Clientes
    Customer vipCustomer = customerRepository.save(Customer.builder()
      .name("Empresa Innovadora S.A.")
      .email("compras@innovadora.com")
      .phone("555-112233")
      .address("Centro Empresarial Torre 2")
      .build());

    Customer regularCustomer = customerRepository.save(Customer.builder()
      .name("Juan Pérez")
      .email("juan.perez@email.com")
      .phone("555-998877")
      .address("Calle Principal 456")
      .build());

    // 4. Crear Productos
    Product laptop = productRepository.save(Product.builder()
      .name("Laptop Pro 15")
      .details("16GB RAM, 512GB SSD")
      .category(electronics)
      .build());

    Product tomato = productRepository.save(Product.builder()
      .name("Tomate Saladette")
      .details("Caja de 10 Kg, calidad premium")
      .category(groceries)
      .build());

    // 5. Crear Entradas de Inventario (Lotes)
    StockEntry laptopEntry = stockEntryRepository.save(StockEntry.builder()
      .product(laptop)
      .supplier(techSupplier)
      .initialStock(50)
      .currentStock(50)
      .purchasePrice(new BigDecimal("800.00"))
      .salePrice(new BigDecimal("1200.00"))
      .enteredBy("Admin")
      .entryDate(LocalDateTime.now().minusDays(10)) // Entró hace 10 días
      .receiptUrl("https://ejemplo.com/ticket-laptop.pdf")
      .build());

    StockEntry tomatoEntry = stockEntryRepository.save(StockEntry.builder()
      .product(tomato)
      .supplier(freshSupplier)
      .initialStock(200)
      .currentStock(200)
      .purchasePrice(new BigDecimal("15.50"))
      .salePrice(new BigDecimal("25.00"))
      .enteredBy("Admin")
      .entryDate(LocalDateTime.now().minusDays(2))
      .build());

    // 6. Crear una Venta Simulada (Invoice)
    InvoiceItem item1 = InvoiceItem.builder()
      .stockEntry(laptopEntry)
      .quantity(2)
      .unitPrice(new BigDecimal("1200.00"))
      .discount(BigDecimal.ZERO)
      .discountType(DiscountType.FIXED_AMOUNT)
      .subTotal(new BigDecimal("2400.00"))
      .build();

    Payment initialPayment = Payment.builder()
      .amount(new BigDecimal("1000.00"))
      .method(PaymentMethod.BANK_TRANSFER)
      .bankReference("TRANSF-998877")
      .paymentDate(LocalDateTime.now())
      .build();

    Invoice invoice = Invoice.builder()
      .invoiceNumber("FAC-MOCK-001")
      .issueDate(LocalDateTime.now())
      .customer(vipCustomer)
      .items(List.of(item1))
      .totalGross(new BigDecimal("2400.00"))
      .totalDiscount(BigDecimal.ZERO)
      .netAmount(new BigDecimal("2400.00"))
      .balanceDue(new BigDecimal("1400.00")) // Queda a deber 1400
      .status(InvoiceStatus.PARTIAL_PAID)
      .payments(List.of(initialPayment)) // Registramos el abono inicial
      .build();

    invoiceRepository.save(invoice);

    // Actualizar el stock del lote tras la venta
    laptopEntry.setCurrentStock(48);
    stockEntryRepository.save(laptopEntry);

    log.info("¡Mock Data inyectada con éxito! La base de datos está lista para pruebas.");
  }
}