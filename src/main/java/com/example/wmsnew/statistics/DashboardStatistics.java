package com.example.wmsnew.statistics;

public class DashboardStatistics {
    private Long totalUsers;
    private Long totalWarehouses;
    private Long totalEmployees;
    private Long totalSuppliers;
    private Long totalCustomers;
    private Long activeEmployees;
    private Long warehousesWithHighCapacity;
    private Long suppliersWithActiveContracts;
    private Long customersWithRecentOrders;
    
    // Growth statistics
    private Double usersGrowthPercentage;
    private Double warehousesGrowthPercentage;
    private Double employeesGrowthPercentage;
    private Double suppliersGrowthPercentage;

    public DashboardStatistics() {}

    public DashboardStatistics(Long totalUsers, Long totalWarehouses, Long totalEmployees, 
                             Long totalSuppliers, Long totalCustomers, Long activeEmployees,
                             Long warehousesWithHighCapacity, Long suppliersWithActiveContracts,
                             Long customersWithRecentOrders) {
        this.totalUsers = totalUsers;
        this.totalWarehouses = totalWarehouses;
        this.totalEmployees = totalEmployees;
        this.totalSuppliers = totalSuppliers;
        this.totalCustomers = totalCustomers;
        this.activeEmployees = activeEmployees;
        this.warehousesWithHighCapacity = warehousesWithHighCapacity;
        this.suppliersWithActiveContracts = suppliersWithActiveContracts;
        this.customersWithRecentOrders = customersWithRecentOrders;
    }

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalWarehouses() {
        return totalWarehouses;
    }

    public void setTotalWarehouses(Long totalWarehouses) {
        this.totalWarehouses = totalWarehouses;
    }

    public Long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(Long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public Long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(Long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(Long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public Long getWarehousesWithHighCapacity() {
        return warehousesWithHighCapacity;
    }

    public void setWarehousesWithHighCapacity(Long warehousesWithHighCapacity) {
        this.warehousesWithHighCapacity = warehousesWithHighCapacity;
    }

    public Long getSuppliersWithActiveContracts() {
        return suppliersWithActiveContracts;
    }

    public void setSuppliersWithActiveContracts(Long suppliersWithActiveContracts) {
        this.suppliersWithActiveContracts = suppliersWithActiveContracts;
    }

    public Long getCustomersWithRecentOrders() {
        return customersWithRecentOrders;
    }

    public void setCustomersWithRecentOrders(Long customersWithRecentOrders) {
        this.customersWithRecentOrders = customersWithRecentOrders;
    }
    
    public Double getUsersGrowthPercentage() {
        return usersGrowthPercentage;
    }

    public void setUsersGrowthPercentage(Double usersGrowthPercentage) {
        this.usersGrowthPercentage = usersGrowthPercentage;
    }

    public Double getWarehousesGrowthPercentage() {
        return warehousesGrowthPercentage;
    }

    public void setWarehousesGrowthPercentage(Double warehousesGrowthPercentage) {
        this.warehousesGrowthPercentage = warehousesGrowthPercentage;
    }

    public Double getEmployeesGrowthPercentage() {
        return employeesGrowthPercentage;
    }

    public void setEmployeesGrowthPercentage(Double employeesGrowthPercentage) {
        this.employeesGrowthPercentage = employeesGrowthPercentage;
    }

    public Double getSuppliersGrowthPercentage() {
        return suppliersGrowthPercentage;
    }

    public void setSuppliersGrowthPercentage(Double suppliersGrowthPercentage) {
        this.suppliersGrowthPercentage = suppliersGrowthPercentage;
    }
}
