package com.example.wmsnew.config;

import com.example.wmsnew.common.enums.UserRole;
import com.example.wmsnew.customer.entity.Customer;
import com.example.wmsnew.customer.repository.CustomerRepository;
import com.example.wmsnew.supplier.entity.Supplier;
import com.example.wmsnew.supplier.repository.SupplierRepository;
import com.example.wmsnew.user.entity.User;
import com.example.wmsnew.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@AllArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;

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
        };
    }
}