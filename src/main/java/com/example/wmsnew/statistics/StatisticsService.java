package com.example.wmsnew.statistics;

import com.example.wmsnew.user.repository.UserRepository;
import com.example.wmsnew.warehouse.repository.WarehouseRepository;
import com.example.wmsnew.supplier.repository.SupplierRepository;
import com.example.wmsnew.customer.repository.CustomerRepository;
import com.example.wmsnew.common.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public DashboardStatistics getDashboardStatistics() {
        Long totalUsers = userRepository.count();
        Long totalWarehouses = warehouseRepository.count();
        Long totalSuppliers = supplierRepository.count();
        Long totalCustomers = customerRepository.count();

        // Count employees (users with STORER and PICKER roles)
        Long totalEmployees = userRepository.countByRole(UserRole.STORER) + userRepository.countByRole(UserRole.PICKER);

        // Additional useful statistics
        Long activeEmployees = userRepository.countByRoleAndIsActiveTrue(UserRole.STORER) + userRepository.countByRoleAndIsActiveTrue(UserRole.PICKER);

        return new DashboardStatistics(
            totalUsers,
            totalWarehouses,
            totalEmployees,
            totalSuppliers,
            totalCustomers,
            activeEmployees,
            0L, // warehousesWithHighCapacity - placeholder
            0L, // suppliersWithActiveContracts - placeholder
            0L  // customersWithRecentOrders - placeholder
        );
    }
}