package com.example.wmsnew.config;

import com.example.wmsnew.common.enums.UserRole;
import com.example.wmsnew.customer.entity.Customer;
import com.example.wmsnew.customer.repository.CustomerRepository;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.supplier.repository.SupplierRepository;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.user.repository.UserRepository;
import com.example.wmsnew.product.entity.Category;
import com.example.wmsnew.product.entity.Product;
import com.example.wmsnew.product.repository.CategoryRepository;
import com.example.wmsnew.product.repository.ProductRepository;
import com.example.wmsnew.warehouse.entity.StandardSizes;
import com.example.wmsnew.warehouse.entity.Warehouse;
import com.example.wmsnew.warehouse.entity.Location;
import com.example.wmsnew.warehouse.repository.StandardSizesRepository;
import com.example.wmsnew.warehouse.repository.WarehouseRepository;
import com.example.wmsnew.warehouse.repository.LocationRepository;
import com.example.wmsnew.inventory.entity.Inventory;
import com.example.wmsnew.inventory.repository.InventoryRepository;
import com.example.wmsnew.shipment.entity.Shipment;
import com.example.wmsnew.shipment.entity.ShipmentItems;
import com.example.wmsnew.shipment.repository.ShipmentRepository;
import com.example.wmsnew.shipment.repository.ShipmentItemsRepository;
import com.example.wmsnew.common.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@AllArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final StandardSizesRepository standardSizesRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentItemsRepository shipmentItemsRepository;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            // Check if users already exist to avoid duplicate seeding
            if (userRepository.count() == 0) {
                List<User> users = List.of(
                    User.builder()
                        .name("John Doe")
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@warehouse.com")
                        .phoneNumber("+1234567890")
                        .role(UserRole.PICKER)
                        .isActive(true)
                        .auth0Id("auth0|john_doe_fake_id")
                        .build(),
                        
                    User.builder()
                        .name("Jane Smith")
                        .firstName("Jane")
                        .lastName("Smith")
                        .email("jane.smith@warehouse.com")
                        .phoneNumber("+1234567891")
                        .role(UserRole.STORER)
                        .isActive(true)
                        .auth0Id("auth0|jane_smith_fake_id")
                        .build(),
                        
                    User.builder()
                        .name("Mike Johnson")
                        .firstName("Mike")
                        .lastName("Johnson")
                        .email("mike.johnson@warehouse.com")
                        .phoneNumber("+1234567892")
                        .role(UserRole.MANAGER)
                        .isActive(true)
                        .auth0Id("auth0|mike_johnson_fake_id")
                        .build(),
                        
                    User.builder()
                        .name("Sarah Wilson")
                        .firstName("Sarah")
                        .lastName("Wilson")
                        .email("sarah.wilson@warehouse.com")
                        .phoneNumber("+1234567893")
                        .role(UserRole.ADMIN)
                        .isActive(true)
                        .auth0Id("auth0|sarah_wilson_fake_id")
                        .build(),
                        
                    User.builder()
                        .name("Bob Brown")
                        .firstName("Bob")
                        .lastName("Brown")
                        .email("bob.brown@warehouse.com")
                        .phoneNumber("+1234567894")
                        .role(UserRole.PICKER)
                        .isActive(true)
                        .auth0Id("auth0|bob_brown_fake_id")
                        .build(),
                        
                    User.builder()
                        .name("Alice Davis")
                        .firstName("Alice")
                        .lastName("Davis")
                        .email("alice.davis@warehouse.com")
                        .phoneNumber("+1234567895")
                        .role(UserRole.STORER)
                        .isActive(false)
                        .auth0Id("auth0|alice_davis_fake_id")
                        .build(),
                        
                    User.builder()
                        .name("Tom Anderson")
                        .firstName("Tom")
                        .lastName("Anderson")
                        .email("tom.anderson@warehouse.com")
                        .phoneNumber("+1234567896")
                        .role(UserRole.MANAGER)
                        .isActive(true)
                        .auth0Id("auth0|tom_anderson_fake_id")
                        .build(),
                        
                    User.builder()
                        .name("Emma Thompson")
                        .firstName("Emma")
                        .lastName("Thompson")
                        .email("emma.thompson@warehouse.com")
                        .phoneNumber("+1234567897")
                        .role(UserRole.PICKER)
                        .isActive(true)
                        .auth0Id("auth0|emma_thompson_fake_id")
                        .build()
                );
                
                userRepository.saveAll(users);
                System.out.println("✅ Seeded " + users.size() + " employees to the database");
            } else {
                System.out.println("📊 Database already contains " + userRepository.count() + " users, skipping seeding");
            }

            // Seed Suppliers
            if (supplierRepository.count() == 0) {
                List<Supplier> suppliers = List.of(
                    Supplier.builder()
                        .name("TechCorp Solutions")
                        .contactPerson("John Smith")
                        .email("john.smith@techcorp.com")
                        .phoneNumber("+1-555-0101")
                        .address("123 Technology Dr")
                        .city("San Francisco")
                        .state("CA")
                        .build(),
                        
                    Supplier.builder()
                        .name("Global Electronics Inc")
                        .contactPerson("Sarah Johnson")
                        .email("sarah.johnson@globalelec.com")
                        .phoneNumber("+1-555-0102")
                        .address("456 Innovation Blvd")
                        .city("Austin")
                        .state("TX")
                        .build(),
                        
                    Supplier.builder()
                        .name("Premium Parts Co")
                        .contactPerson("Michael Chen")
                        .email("m.chen@premiumparts.com")
                        .phoneNumber("+1-555-0103")
                        .address("789 Industrial Way")
                        .city("Detroit")
                        .state("MI")
                        .build(),
                        
                    Supplier.builder()
                        .name("Swift Logistics Ltd")
                        .contactPerson("Emma Wilson")
                        .email("emma.wilson@swiftlogistics.com")
                        .phoneNumber("+1-555-0104")
                        .address("321 Commerce St")
                        .city("Chicago")
                        .state("IL")
                        .build(),
                        
                    Supplier.builder()
                        .name("Quality Components LLC")
                        .contactPerson("David Brown")
                        .email("david.brown@qualitycomp.com")
                        .phoneNumber("+1-555-0105")
                        .address("654 Manufacturing Ave")
                        .city("Phoenix")
                        .state("AZ")
                        .build()
                );
                
                supplierRepository.saveAll(suppliers);
                System.out.println("✅ Seeded " + suppliers.size() + " suppliers to the database");
            } else {
                System.out.println("📊 Database already contains " + supplierRepository.count() + " suppliers, skipping seeding");
            }

            // Seed Customers
            if (customerRepository.count() == 0) {
                List<Customer> customers = List.of(
                    Customer.builder()
                        .name("Acme Corporation")
                        .email("orders@acmecorp.com")
                        .phoneNumber("+1-555-1001")
                        .address("100 Business Plaza")
                        .city("New York")
                        .state("NY")
                        .build(),
                        
                    Customer.builder()
                        .name("Retail Solutions Inc")
                        .email("purchasing@retailsolutions.com")
                        .phoneNumber("+1-555-1002")
                        .address("200 Retail Center")
                        .city("Los Angeles")
                        .state("CA")
                        .build(),
                        
                    Customer.builder()
                        .name("Metro Distribution")
                        .email("orders@metrodist.com")
                        .phoneNumber("+1-555-1003")
                        .address("300 Distribution Hub")
                        .city("Denver")
                        .state("CO")
                        .build(),
                        
                    Customer.builder()
                        .name("Enterprise Systems LLC")
                        .email("procurement@enterprisesys.com")
                        .phoneNumber("+1-555-1004")
                        .address("400 Corporate Blvd")
                        .city("Atlanta")
                        .state("GA")
                        .build(),
                        
                    Customer.builder()
                        .name("Fast Track Retail")
                        .email("buyers@fasttrackretail.com")
                        .phoneNumber("+1-555-1005")
                        .address("500 Commerce Way")
                        .city("Seattle")
                        .state("WA")
                        .build(),
                        
                    Customer.builder()
                        .name("Prime Wholesale Co")
                        .email("orders@primewholesale.com")
                        .phoneNumber("+1-555-1006")
                        .address("600 Wholesale Dr")
                        .city("Miami")
                        .state("FL")
                        .build()
                );
                
                customerRepository.saveAll(customers);
                System.out.println("✅ Seeded " + customers.size() + " customers to the database");
            } else {
                System.out.println("📊 Database already contains " + customerRepository.count() + " customers, skipping seeding");
            }

            // Seed Categories
            if (categoryRepository.count() <= 1) {
                List<Category> categories = List.of(
                    Category.builder().name("Electronics").build(),
                    Category.builder().name("Clothing").build(),
                    Category.builder().name("Home & Garden").build(),
                    Category.builder().name("Sports & Outdoors").build(),
                    Category.builder().name("Books").build(),
                    Category.builder().name("Toys & Games").build(),
                    Category.builder().name("Health & Beauty").build(),
                    Category.builder().name("Automotive").build(),
                    Category.builder().name("Food & Beverages").build(),
                    Category.builder().name("Office Supplies").build()
                );
                
                categoryRepository.saveAll(categories);
                System.out.println("✅ Seeded " + categories.size() + " categories to the database");
            } else {
                System.out.println("📊 Database already contains " + categoryRepository.count() + " categories, skipping seeding");
            }

            // Seed Standard Sizes
            if (standardSizesRepository.count() <= 2) {
                List<StandardSizes> standardSizes = List.of(
                    new StandardSizes(null, 10, 10, 10, 1000),  // Small bin
                    new StandardSizes(null, 20, 15, 12, 3600),  // Medium bin
                    new StandardSizes(null, 30, 20, 15, 9000),  // Large bin
                    new StandardSizes(null, 40, 30, 20, 24000), // Extra large bin
                    new StandardSizes(null, 120, 80, 100, 960000), // Pallet size
                    new StandardSizes(null, 15, 15, 8, 1800),   // Document box
                    new StandardSizes(null, 25, 25, 20, 12500), // Storage box
                    new StandardSizes(null, 50, 40, 30, 60000)  // Bulk storage
                );
                
                standardSizesRepository.saveAll(standardSizes);
                System.out.println("✅ Seeded " + standardSizes.size() + " standard sizes to the database");
            } else {
                System.out.println("📊 Database already contains " + standardSizesRepository.count() + " standard sizes, skipping seeding");
            }

            // Seed Products
            if (productRepository.count() == 0) {
                List<Category> categories = categoryRepository.findAll();
                if (!categories.isEmpty()) {
                    List<Product> products = List.of(
                        Product.builder()
                            .productName("iPhone 14 Pro")
                            .brandName("Apple")
                            .description("Latest iPhone with 48MP camera and A16 Bionic chip")
                            .price(new BigDecimal("999.99"))
                            .category(categories.get(0)) // Electronics
                            .build(),
                        Product.builder()
                            .productName("Samsung Galaxy S23")
                            .brandName("Samsung")
                            .description("Flagship Android phone with 200MP camera")
                            .price(new BigDecimal("899.99"))
                            .category(categories.get(0)) // Electronics
                            .build(),
                        Product.builder()
                            .productName("Nike Air Max 270")
                            .brandName("Nike")
                            .description("Comfortable running shoes with Max Air technology")
                            .price(new BigDecimal("149.99"))
                            .category(categories.get(3)) // Sports & Outdoors
                            .build(),
                        Product.builder()
                            .productName("Levi's 501 Jeans")
                            .brandName("Levi's")
                            .description("Classic straight-leg jeans in original fit")
                            .price(new BigDecimal("69.99"))
                            .category(categories.get(1)) // Clothing
                            .build(),
                        Product.builder()
                            .productName("KitchenAid Stand Mixer")
                            .brandName("KitchenAid")
                            .description("Professional grade stand mixer for baking")
                            .price(new BigDecimal("349.99"))
                            .category(categories.get(2)) // Home & Garden
                            .build(),
                        Product.builder()
                            .productName("MacBook Pro 14-inch")
                            .brandName("Apple")
                            .description("Professional laptop with M3 Pro chip")
                            .price(new BigDecimal("1999.99"))
                            .category(categories.get(0)) // Electronics
                            .build(),
                        Product.builder()
                            .productName("The Great Gatsby")
                            .brandName("Scribner")
                            .description("Classic American novel by F. Scott Fitzgerald")
                            .price(new BigDecimal("12.99"))
                            .category(categories.get(4)) // Books
                            .build(),
                        Product.builder()
                            .productName("LEGO Creator 3-in-1 Deep Sea Creatures")
                            .brandName("LEGO")
                            .description("Build a shark, squid, or angler fish")
                            .price(new BigDecimal("79.99"))
                            .category(categories.get(5)) // Toys & Games
                            .build(),
                        Product.builder()
                            .productName("L'Oreal Paris Revitalift Serum")
                            .brandName("L'Oreal")
                            .description("Anti-aging serum with hyaluronic acid")
                            .price(new BigDecimal("24.99"))
                            .category(categories.get(6)) // Health & Beauty
                            .build(),
                        Product.builder()
                            .productName("Michelin Pilot Sport Tires")
                            .brandName("Michelin")
                            .description("High performance tires for sports cars")
                            .price(new BigDecimal("299.99"))
                            .category(categories.get(7)) // Automotive
                            .build(),
                        Product.builder()
                            .productName("Organic Coffee Beans")
                            .brandName("Blue Bottle")
                            .description("Premium single-origin coffee beans")
                            .price(new BigDecimal("18.99"))
                            .category(categories.get(8)) // Food & Beverages
                            .build(),
                        Product.builder()
                            .productName("Staples Copy Paper")
                            .brandName("Staples")
                            .description("500 sheets of 8.5x11 white copy paper")
                            .price(new BigDecimal("9.99"))
                            .category(categories.get(9)) // Office Supplies
                            .build()
                    );
                    
                    productRepository.saveAll(products);
                    System.out.println("✅ Seeded " + products.size() + " products to the database");
                } else {
                    System.out.println("⚠️ No categories found, skipping product seeding");
                }
            } else {
                System.out.println("📊 Database already contains " + productRepository.count() + " products, skipping seeding");
            }

            // Seed Warehouses (needed for locations and inventory)
            if (warehouseRepository.count() == 0) {
                List<Warehouse> warehouses = List.of(
                    Warehouse.builder()
                        .warehouseName("Main Warehouse")
                        .build(),
                    Warehouse.builder()
                        .warehouseName("West Coast Warehouse")
                        .build()
                );
                
                warehouseRepository.saveAll(warehouses);
                System.out.println("✅ Seeded " + warehouses.size() + " warehouses to the database");
            } else {
                System.out.println("📊 Database already contains " + warehouseRepository.count() + " warehouses, skipping seeding");
            }

            // Seed Locations (needed for inventory)
            if (locationRepository.count() == 0) {
                List<Warehouse> warehouses = warehouseRepository.findAll();
                List<StandardSizes> sizes = standardSizesRepository.findAll();
                
                if (!warehouses.isEmpty() && !sizes.isEmpty()) {
                    List<Location> locations = List.of(
                        Location.builder()
                            .locationCode("A-01-01")
                            .warehouse(warehouses.get(0))
                            .standardSize(sizes.get(0)) // Small bin
                            .asile("A")
                            .rack("01")
                            .shelf("01")
                            .bin("01")
                            .currentLoad(0.0)
                            .build(),
                        Location.builder()
                            .locationCode("A-01-02")
                            .warehouse(warehouses.get(0))
                            .standardSize(sizes.get(1)) // Medium bin
                            .asile("A")
                            .rack("01")
                            .shelf("02")
                            .bin("01")
                            .currentLoad(0.0)
                            .build(),
                        Location.builder()
                            .locationCode("A-02-01")
                            .warehouse(warehouses.get(0))
                            .standardSize(sizes.get(2)) // Large bin
                            .asile("A")
                            .rack("02")
                            .shelf("01")
                            .bin("01")
                            .currentLoad(0.0)
                            .build(),
                        Location.builder()
                            .locationCode("B-01-01")
                            .warehouse(warehouses.get(1))
                            .standardSize(sizes.get(0)) // Small bin
                            .asile("B")
                            .rack("01")
                            .shelf("01")
                            .bin("01")
                            .currentLoad(0.0)
                            .build(),
                        Location.builder()
                            .locationCode("B-01-02")
                            .warehouse(warehouses.get(1))
                            .standardSize(sizes.get(1)) // Medium bin
                            .asile("B")
                            .rack("01")
                            .shelf("02")
                            .bin("01")
                            .currentLoad(0.0)
                            .build()
                    );
                    
                    locationRepository.saveAll(locations);
                    System.out.println("✅ Seeded " + locations.size() + " locations to the database");
                } else {
                    System.out.println("⚠️ No warehouses or standard sizes found, skipping location seeding");
                }
            } else {
                System.out.println("📊 Database already contains " + locationRepository.count() + " locations, skipping seeding");
            }

            // Seed Inventory
            if (inventoryRepository.count() == 0) {
                List<Product> products = productRepository.findAll();
                List<Location> locations = locationRepository.findAll();
                
                if (!products.isEmpty() && !locations.isEmpty()) {
                    List<Inventory> inventories = List.of(
                        Inventory.builder()
                            .product(products.get(0)) // iPhone 14 Pro
                            .location(locations.get(0))
                            .quantity(50)
                            .batchNumber("BATCH001")
                            .manufacturingDate(LocalDate.now().minusMonths(2))
                            .expiryDate(null) // Electronics don't expire
                            .build(),
                        Inventory.builder()
                            .product(products.get(1)) // Samsung Galaxy S23
                            .location(locations.get(1))
                            .quantity(75)
                            .batchNumber("BATCH002")
                            .manufacturingDate(LocalDate.now().minusMonths(1))
                            .expiryDate(null) // Electronics don't expire
                            .build(),
                        Inventory.builder()
                            .product(products.get(2)) // Nike Air Max 270
                            .location(locations.get(2))
                            .quantity(120)
                            .batchNumber("BATCH003")
                            .manufacturingDate(LocalDate.now().minusMonths(3))
                            .expiryDate(null) // Shoes don't expire
                            .build(),
                        Inventory.builder()
                            .product(products.get(3)) // Levi's 501 Jeans
                            .location(locations.get(0))
                            .quantity(200)
                            .batchNumber("BATCH004")
                            .manufacturingDate(LocalDate.now().minusMonths(4))
                            .expiryDate(null) // Clothing doesn't expire
                            .build(),
                        Inventory.builder()
                            .product(products.get(4)) // KitchenAid Stand Mixer
                            .location(locations.get(1))
                            .quantity(25)
                            .batchNumber("BATCH005")
                            .manufacturingDate(LocalDate.now().minusMonths(1))
                            .expiryDate(null) // Appliances don't expire
                            .build(),
                        Inventory.builder()
                            .product(products.get(10)) // Organic Coffee Beans
                            .location(locations.get(2))
                            .quantity(500)
                            .batchNumber("BATCH006")
                            .manufacturingDate(LocalDate.now().minusWeeks(2))
                            .expiryDate(LocalDate.now().plusYears(1)) // Coffee expires
                            .build()
                    );
                    
                    inventoryRepository.saveAll(inventories);
                    System.out.println("✅ Seeded " + inventories.size() + " inventory records to the database");
                } else {
                    System.out.println("⚠️ No products or locations found, skipping inventory seeding");
                }
            } else {
                System.out.println("📊 Database already contains " + inventoryRepository.count() + " inventory records, skipping seeding");
            }

            // Seed Shipments
            if (shipmentRepository.count() == 0) {
                List<Supplier> suppliers = supplierRepository.findAll();
                List<Product> products = productRepository.findAll();
                List<User> storers = userRepository.findByRole(UserRole.STORER);
                
                if (!suppliers.isEmpty() && !products.isEmpty()) {
                    List<Shipment> shipments = List.of(
                        Shipment.builder()
                            .shipmentNumber("SHP-2024-001")
                            .supplier(suppliers.get(0))
                            .status(ShipmentStatus.PENDING)
                            .totalPrice(15000)
                            .build(),
                        Shipment.builder()
                            .shipmentNumber("SHP-2024-002")
                            .supplier(suppliers.get(1))
                            .status(ShipmentStatus.ASSIGNED)
                            .storer(!storers.isEmpty() ? storers.get(0) : null)
                            .totalPrice(22500)
                            .build(),
                        Shipment.builder()
                            .shipmentNumber("SHP-2024-003")
                            .supplier(suppliers.get(2))
                            .status(ShipmentStatus.STORED)
                            .storer(!storers.isEmpty() ? storers.get(1) : null)
                            .totalPrice(8750)
                            .build()
                    );
                    
                    List<Shipment> savedShipments = shipmentRepository.saveAll(shipments);
                    
                    // Add shipment items
                    List<ShipmentItems> shipmentItems = List.of(
                        ShipmentItems.builder()
                            .shipment(savedShipments.get(0))
                            .product(products.get(0)) // iPhone 14 Pro
                            .quantity(15)
                            .batchNumber("SHP-1-ITM-1-20240824")
                            .manufacturingDate(LocalDate.now().minusDays(30))
                            .unitCost(new BigDecimal("999.99"))
                            .build(),
                        ShipmentItems.builder()
                            .shipment(savedShipments.get(1))
                            .product(products.get(1)) // Samsung Galaxy S23
                            .quantity(25)
                            .batchNumber("SHP-2-ITM-2-20240824")
                            .manufacturingDate(LocalDate.now().minusDays(45))
                            .unitCost(new BigDecimal("899.99"))
                            .build(),
                        ShipmentItems.builder()
                            .shipment(savedShipments.get(2))
                            .product(products.get(2)) // Nike Air Max 270
                            .quantity(50)
                            .batchNumber("SHP-3-ITM-3-20240824")
                            .manufacturingDate(LocalDate.now().minusDays(20))
                            .unitCost(new BigDecimal("149.99"))
                            .build(),
                        ShipmentItems.builder()
                            .shipment(savedShipments.get(0))
                            .product(products.get(3)) // Levi's 501 Jeans
                            .quantity(100)
                            .batchNumber("SHP-1-ITM-4-20240824")
                            .manufacturingDate(LocalDate.now().minusDays(60))
                            .unitCost(new BigDecimal("69.99"))
                            .build()
                    );
                    
                    shipmentItemsRepository.saveAll(shipmentItems);
                    System.out.println("✅ Seeded " + savedShipments.size() + " shipments with " + shipmentItems.size() + " items to the database");
                } else {
                    System.out.println("⚠️ No suppliers or products found, skipping shipment seeding");
                }
            } else {
                System.out.println("📊 Database already contains " + shipmentRepository.count() + " shipments, skipping seeding");
            }
        };
    }
}