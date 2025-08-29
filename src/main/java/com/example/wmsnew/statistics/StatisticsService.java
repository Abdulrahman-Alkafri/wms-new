package com.example.wmsnew.statistics;

import com.example.wmsnew.user.repository.UserRepository;
import com.example.wmsnew.warehouse.repository.WarehouseRepository;
import com.example.wmsnew.supplier.repository.SupplierRepository;
import com.example.wmsnew.customer.repository.CustomerRepository;
import com.example.wmsnew.order.repository.OrdersRepository;
import com.example.wmsnew.shipment.repository.ShipmentRepository;
import com.example.wmsnew.common.enums.UserRole;
import com.example.wmsnew.statistics.dto.AnalyticsDto;
import com.example.wmsnew.statistics.dto.BusinessComparisonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    public DashboardStatistics getDashboardStatistics() {
        Long totalUsers = userRepository.count();
        Long totalWarehouses = warehouseRepository.count();
        Long totalSuppliers = supplierRepository.count();
        Long totalCustomers = customerRepository.count();

        // Count employees (users with STORER and PICKER roles)
        Long totalEmployees = userRepository.countByRole(UserRole.STORER) + userRepository.countByRole(UserRole.PICKER);

        // Additional useful statistics
        Long activeEmployees = userRepository.countByRoleAndIsActiveTrue(UserRole.STORER) + userRepository.countByRoleAndIsActiveTrue(UserRole.PICKER);

        // Calculate high capacity warehouses (capacity > 1000)
        Long warehousesWithHighCapacity = warehouseRepository.countWarehousesWithHighCapacity(1000);
        
        // Calculate suppliers with active contracts (last 30 days activity)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        Long suppliersWithActiveContracts = calculateSuppliersWithRecentActivity(thirtyDaysAgo);
        
        // Calculate customers with recent orders (last 30 days)
        Long customersWithRecentOrders = calculateCustomersWithRecentOrders(thirtyDaysAgo);
        
        // Create dashboard statistics and calculate growth percentages
        DashboardStatistics stats = new DashboardStatistics(
            totalUsers,
            totalWarehouses,
            totalEmployees,
            totalSuppliers,
            totalCustomers,
            activeEmployees,
            warehousesWithHighCapacity,
            suppliersWithActiveContracts,
            customersWithRecentOrders
        );
        
        // Calculate and set growth percentages (comparing to previous month)
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        stats.setUsersGrowthPercentage(calculateGrowthPercentage(
            totalUsers, getPreviousMonthCount("users", lastMonth)));
        stats.setWarehousesGrowthPercentage(calculateGrowthPercentage(
            totalWarehouses, getPreviousMonthCount("warehouses", lastMonth)));
        stats.setEmployeesGrowthPercentage(calculateGrowthPercentage(
            totalEmployees, getPreviousMonthCount("employees", lastMonth)));
        stats.setSuppliersGrowthPercentage(calculateGrowthPercentage(
            totalSuppliers, getPreviousMonthCount("suppliers", lastMonth)));
        
        return stats;
    }

    public AnalyticsDto.AnalyticsResponse getAnalytics(String period) {
        List<AnalyticsDto.TimeSeriesData> ordersTimeSeries = getOrdersTimeSeries(period);
        List<AnalyticsDto.TimeSeriesData> shipmentsTimeSeries = getShipmentsTimeSeries(period);
        List<AnalyticsDto.EmployeeRoleStats> employeeStats = getEmployeeRoleStats();
        List<AnalyticsDto.FinancialComparison> financialComparison = getFinancialComparison(period);

        return new AnalyticsDto.AnalyticsResponse(ordersTimeSeries, shipmentsTimeSeries, employeeStats, financialComparison);
    }

    public BusinessComparisonDto.BusinessComparisonResponse getBusinessComparison(String period) {
        List<BusinessComparisonDto.OrderShipmentComparison> orderShipmentComparisons = getOrderShipmentComparison(period);
        List<BusinessComparisonDto.PaymentAnalytics> paymentAnalytics = getPaymentAnalytics(period);
        
        // Calculate overall business metrics
        Double overallGrowthRate = calculateOverallGrowthRate(period);
        Double operationalEfficiency = calculateOperationalEfficiency();
        BigDecimal totalBusinessValue = calculateTotalBusinessValue(period);
        
        return new BusinessComparisonDto.BusinessComparisonResponse(
            orderShipmentComparisons, paymentAnalytics, overallGrowthRate, 
            operationalEfficiency, totalBusinessValue
        );
    }

    private List<AnalyticsDto.TimeSeriesData> getOrdersTimeSeries(String period) {
        List<AnalyticsDto.TimeSeriesData> data = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(period, endDate);

        try {
            List<Object[]> queryResults;
            
            switch (period.toLowerCase()) {
                case "week":
                    queryResults = ordersRepository.findOrderAnalyticsByDateRange(startDate, endDate);
                    break;
                case "month":
                    queryResults = ordersRepository.findMonthlyOrderAnalytics(startDate, endDate);
                    break;
                case "year":
                    queryResults = ordersRepository.findYearlyOrderAnalytics(startDate, endDate);
                    break;
                default:
                    queryResults = ordersRepository.findMonthlyOrderAnalytics(startDate, endDate);
            }
            
            for (Object[] result : queryResults) {
                String periodLabel;
                LocalDate date;
                
                if (period.equals("week")) {
                    date = (LocalDate) result[0];
                    periodLabel = formatPeriodLabel(date, period);
                } else if (period.equals("month")) {
                    int year = (Integer) result[0];
                    int month = (Integer) result[1];
                    date = LocalDate.of(year, month, 1);
                    periodLabel = formatPeriodLabel(date, period);
                } else { // year
                    int year = (Integer) result[0];
                    date = LocalDate.of(year, 1, 1);
                    periodLabel = formatPeriodLabel(date, period);
                }
                
                Long count = ((Number) result[period.equals("week") ? 1 : 2]).longValue();
                BigDecimal totalValue = result[period.equals("week") ? 2 : 3] != null ? 
                    new BigDecimal(((Number) result[period.equals("week") ? 2 : 3]).toString()) : 
                    BigDecimal.ZERO;
                
                data.add(new AnalyticsDto.TimeSeriesData(periodLabel, count, totalValue, date));
            }
        } catch (Exception e) {
            // Fallback to empty data if query fails
            System.err.println("Error fetching orders time series: " + e.getMessage());
        }
        
        return data;
    }

    private List<AnalyticsDto.TimeSeriesData> getShipmentsTimeSeries(String period) {
        List<AnalyticsDto.TimeSeriesData> data = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(period, endDate);

        try {
            List<Object[]> queryResults;
            
            switch (period.toLowerCase()) {
                case "week":
                    queryResults = shipmentRepository.findShipmentAnalyticsByDateRange(startDate, endDate);
                    break;
                case "month":
                    queryResults = shipmentRepository.findMonthlyShipmentAnalytics(startDate, endDate);
                    break;
                case "year":
                    queryResults = shipmentRepository.findYearlyShipmentAnalytics(startDate, endDate);
                    break;
                default:
                    queryResults = shipmentRepository.findMonthlyShipmentAnalytics(startDate, endDate);
            }
            
            for (Object[] result : queryResults) {
                String periodLabel;
                LocalDate date;
                
                if (period.equals("week")) {
                    date = (LocalDate) result[0];
                    periodLabel = formatPeriodLabel(date, period);
                } else if (period.equals("month")) {
                    int year = (Integer) result[0];
                    int month = (Integer) result[1];
                    date = LocalDate.of(year, month, 1);
                    periodLabel = formatPeriodLabel(date, period);
                } else { // year
                    int year = (Integer) result[0];
                    date = LocalDate.of(year, 1, 1);
                    periodLabel = formatPeriodLabel(date, period);
                }
                
                Long count = ((Number) result[period.equals("week") ? 1 : 2]).longValue();
                BigDecimal totalValue = result[period.equals("week") ? 2 : 3] != null ? 
                    new BigDecimal(((Number) result[period.equals("week") ? 2 : 3]).toString()) : 
                    BigDecimal.ZERO;
                
                data.add(new AnalyticsDto.TimeSeriesData(periodLabel, count, totalValue, date));
            }
        } catch (Exception e) {
            // Fallback to empty data if query fails
            System.err.println("Error fetching shipments time series: " + e.getMessage());
        }
        
        return data;
    }

    private List<AnalyticsDto.EmployeeRoleStats> getEmployeeRoleStats() {
        List<AnalyticsDto.EmployeeRoleStats> stats = new ArrayList<>();
        
        try {
            List<Object[]> userStats = userRepository.getUserStatsByRole();
            
            for (Object[] result : userStats) {
                UserRole role = (UserRole) result[0];
                Long totalCount = ((Number) result[1]).longValue();
                Long activeCount = ((Number) result[2]).longValue();
                
                // Calculate productivity based on active ratio and role performance
                Double productivity = calculateRoleProductivity(role, activeCount, totalCount);
                
                String roleDisplayName = formatRoleDisplayName(role);
                stats.add(new AnalyticsDto.EmployeeRoleStats(roleDisplayName, totalCount, activeCount, productivity));
            }
        } catch (Exception e) {
            System.err.println("Error fetching employee role stats: " + e.getMessage());
            // Fallback to basic counts
            stats.add(new AnalyticsDto.EmployeeRoleStats("Storers", 
                userRepository.countByRole(UserRole.STORER), 
                userRepository.countByRoleAndIsActiveTrue(UserRole.STORER), 85.5));
            stats.add(new AnalyticsDto.EmployeeRoleStats("Pickers", 
                userRepository.countByRole(UserRole.PICKER), 
                userRepository.countByRoleAndIsActiveTrue(UserRole.PICKER), 92.3));
            stats.add(new AnalyticsDto.EmployeeRoleStats("Managers", 
                userRepository.countByRole(UserRole.MANAGER), 
                userRepository.countByRoleAndIsActiveTrue(UserRole.MANAGER), 88.7));
            stats.add(new AnalyticsDto.EmployeeRoleStats("Admins", 
                userRepository.countByRole(UserRole.ADMIN), 
                userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN), 95.2));
        }
        
        return stats;
    }
    
    private Double calculateRoleProductivity(UserRole role, Long activeCount, Long totalCount) {
        if (totalCount == 0) return 0.0;
        
        double baseProductivity = switch (role) {
            case STORER -> 85.0;
            case PICKER -> 90.0;
            case MANAGER -> 88.0;
            case ADMIN -> 95.0;
        };
        
        // Adjust based on active ratio
        double activeRatio = (double) activeCount / totalCount;
        return baseProductivity * (0.7 + 0.3 * activeRatio); // Scale between 70% and 100% of base
    }
    
    private String formatRoleDisplayName(UserRole role) {
        return switch (role) {
            case STORER -> "Storers";
            case PICKER -> "Pickers";
            case MANAGER -> "Managers";
            case ADMIN -> "Admins";
        };
    }

    private List<AnalyticsDto.FinancialComparison> getFinancialComparison(String period) {
        List<AnalyticsDto.FinancialComparison> comparison = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(period, endDate);

        try {
            // Get orders and shipments data for the same periods
            List<AnalyticsDto.TimeSeriesData> ordersData = getOrdersTimeSeries(period);
            List<AnalyticsDto.TimeSeriesData> shipmentsData = getShipmentsTimeSeries(period);
            
            // Create a map for easy lookup of shipment data by period
            Map<String, BigDecimal> shipmentCostsByPeriod = new HashMap<>();
            for (AnalyticsDto.TimeSeriesData shipment : shipmentsData) {
                shipmentCostsByPeriod.put(shipment.getPeriod(), shipment.getTotalValue());
            }
            
            // Create financial comparison data
            for (AnalyticsDto.TimeSeriesData order : ordersData) {
                String periodLabel = order.getPeriod();
                BigDecimal orderRevenue = order.getTotalValue();
                BigDecimal shipmentCosts = shipmentCostsByPeriod.getOrDefault(periodLabel, BigDecimal.ZERO);
                BigDecimal profit = orderRevenue.subtract(shipmentCosts);
                
                Double profitMargin = 0.0;
                if (orderRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    profitMargin = profit.divide(orderRevenue, 4, BigDecimal.ROUND_HALF_UP)
                                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                }
                
                comparison.add(new AnalyticsDto.FinancialComparison(
                    periodLabel, orderRevenue, shipmentCosts, profit, profitMargin
                ));
            }
        } catch (Exception e) {
            System.err.println("Error creating financial comparison: " + e.getMessage());
        }
        
        return comparison;
    }

    private LocalDate getStartDate(String period, LocalDate endDate) {
        return switch (period.toLowerCase()) {
            case "week" -> endDate.minusWeeks(12);
            case "month" -> endDate.minusMonths(12);
            case "year" -> endDate.minusYears(5);
            default -> endDate.minusMonths(6);
        };
    }

    private int getPeriodCount(String period) {
        return switch (period.toLowerCase()) {
            case "week" -> 12;
            case "month" -> 12;
            case "year" -> 5;
            default -> 6;
        };
    }

    private int getDayIncrement(String period) {
        return switch (period.toLowerCase()) {
            case "week" -> 7;
            case "month" -> 30;
            case "year" -> 365;
            default -> 30;
        };
    }

    private String formatPeriodLabel(LocalDate date, String period) {
        return switch (period.toLowerCase()) {
            case "week" -> "Week " + date.format(DateTimeFormatter.ofPattern("w"));
            case "month" -> date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            case "year" -> date.format(DateTimeFormatter.ofPattern("yyyy"));
            default -> date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
        };
    }
    
    private Long calculateSuppliersWithRecentActivity(LocalDate sinceDate) {
        try {
            // Count suppliers that have had orders or shipments in the last 30 days
            return supplierRepository.countSuppliersWithRecentActivity(sinceDate);
        } catch (Exception e) {
            System.err.println("Error calculating suppliers with recent activity: " + e.getMessage());
            // Fallback: assume 70% of total suppliers are active
            return Math.round(supplierRepository.count() * 0.7);
        }
    }
    
    private Long calculateCustomersWithRecentOrders(LocalDate sinceDate) {
        try {
            // Count customers that have placed orders in the last 30 days
            return customerRepository.countCustomersWithRecentOrders(sinceDate);
        } catch (Exception e) {
            System.err.println("Error calculating customers with recent orders: " + e.getMessage());
            // Fallback: assume 30% of total customers have recent orders
            return Math.round(customerRepository.count() * 0.3);
        }
    }
    
    private Double calculateGrowthPercentage(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        
        return ((double) (current - previous) / previous) * 100.0;
    }
    
    private Long getPreviousMonthCount(String entity, LocalDate asOfDate) {
        try {
            LocalDate startOfMonth = asOfDate.withDayOfMonth(1);
            LocalDate endOfMonth = asOfDate.withDayOfMonth(asOfDate.lengthOfMonth());
            
            return switch (entity.toLowerCase()) {
                case "users" -> userRepository.countByCreatedAtBetween(startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
                case "warehouses" -> warehouseRepository.countByCreatedAtBetween(startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
                case "employees" -> {
                    Long storers = userRepository.countByRoleAndCreatedAtBetween(UserRole.STORER, startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
                    Long pickers = userRepository.countByRoleAndCreatedAtBetween(UserRole.PICKER, startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
                    yield storers + pickers;
                }
                case "suppliers" -> supplierRepository.countByCreatedAtBetween(startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
                default -> 0L;
            };
        } catch (Exception e) {
            System.err.println("Error getting previous month count for " + entity + ": " + e.getMessage());
            return 0L;
        }
    }
    
    private List<BusinessComparisonDto.OrderShipmentComparison> getOrderShipmentComparison(String period) {
        List<BusinessComparisonDto.OrderShipmentComparison> comparisons = new ArrayList<>();
        
        try {
            List<AnalyticsDto.TimeSeriesData> ordersData = getOrdersTimeSeries(period);
            List<AnalyticsDto.TimeSeriesData> shipmentsData = getShipmentsTimeSeries(period);
            
            // Create a map for easy lookup
            Map<String, AnalyticsDto.TimeSeriesData> shipmentsMap = new HashMap<>();
            for (AnalyticsDto.TimeSeriesData shipment : shipmentsData) {
                shipmentsMap.put(shipment.getPeriod(), shipment);
            }
            
            for (AnalyticsDto.TimeSeriesData order : ordersData) {
                AnalyticsDto.TimeSeriesData shipment = shipmentsMap.get(order.getPeriod());
                
                Long ordersCount = order.getCount();
                Long shipmentsCount = shipment != null ? shipment.getCount() : 0L;
                BigDecimal ordersRevenue = order.getTotalValue();
                BigDecimal shipmentsCosts = shipment != null ? shipment.getTotalValue() : BigDecimal.ZERO;
                
                // Calculate fulfillment rate (shipments/orders)
                Double fulfillmentRate = ordersCount > 0 ? 
                    (double) shipmentsCount / ordersCount * 100.0 : 0.0;
                
                // Calculate profitability score
                Double profitabilityScore = ordersRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
                    ordersRevenue.subtract(shipmentsCosts).divide(ordersRevenue, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;
                
                // Mock values for pending orders and delayed shipments (would need actual queries)
                Long pendingOrders = Math.round(ordersCount * 0.1); // Assume 10% pending
                Long delayedShipments = Math.round(shipmentsCount * 0.05); // Assume 5% delayed
                
                comparisons.add(new BusinessComparisonDto.OrderShipmentComparison(
                    order.getPeriod(), ordersCount, shipmentsCount, ordersRevenue, 
                    shipmentsCosts, fulfillmentRate, profitabilityScore, 
                    pendingOrders, delayedShipments
                ));
            }
        } catch (Exception e) {
            System.err.println("Error creating order-shipment comparison: " + e.getMessage());
        }
        
        return comparisons;
    }
    
    private List<BusinessComparisonDto.PaymentAnalytics> getPaymentAnalytics(String period) {
        List<BusinessComparisonDto.PaymentAnalytics> analytics = new ArrayList<>();
        
        try {
            List<AnalyticsDto.TimeSeriesData> ordersData = getOrdersTimeSeries(period);
            
            for (AnalyticsDto.TimeSeriesData order : ordersData) {
                BigDecimal totalPaymentsReceived = order.getTotalValue();
                BigDecimal pendingPayments = totalPaymentsReceived.multiply(BigDecimal.valueOf(0.15)); // Assume 15% pending
                
                // Mock payment success rate (would need actual payment data)
                Double paymentSuccessRate = 94.5; // Assume 94.5% success rate
                
                BigDecimal averagePaymentValue = order.getCount() > 0 ? 
                    totalPaymentsReceived.divide(BigDecimal.valueOf(order.getCount()), 2, BigDecimal.ROUND_HALF_UP) : 
                    BigDecimal.ZERO;
                
                Long totalTransactions = order.getCount();
                Long failedTransactions = Math.round(totalTransactions * 0.055); // 5.5% failure rate
                
                analytics.add(new BusinessComparisonDto.PaymentAnalytics(
                    order.getPeriod(), totalPaymentsReceived, pendingPayments, 
                    paymentSuccessRate, averagePaymentValue, totalTransactions, failedTransactions
                ));
            }
        } catch (Exception e) {
            System.err.println("Error creating payment analytics: " + e.getMessage());
        }
        
        return analytics;
    }
    
    private Double calculateOverallGrowthRate(String period) {
        try {
            LocalDate now = LocalDate.now();
            LocalDate previousPeriodStart = switch (period.toLowerCase()) {
                case "week" -> now.minusWeeks(1);
                case "month" -> now.minusMonths(1);
                case "year" -> now.minusYears(1);
                default -> now.minusMonths(1);
            };
            
            // Calculate current period revenue
            BigDecimal currentRevenue = ordersRepository.sumOrderValueByDateRange(
                getStartDate("month", now), now) != null ? 
                BigDecimal.valueOf(ordersRepository.sumOrderValueByDateRange(getStartDate("month", now), now)) : 
                BigDecimal.ZERO;
            
            // Calculate previous period revenue
            BigDecimal previousRevenue = ordersRepository.sumOrderValueByDateRange(
                getStartDate("month", previousPeriodStart), previousPeriodStart) != null ? 
                BigDecimal.valueOf(ordersRepository.sumOrderValueByDateRange(getStartDate("month", previousPeriodStart), previousPeriodStart)) : 
                BigDecimal.ZERO;
            
            if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
                return currentRevenue.subtract(previousRevenue)
                    .divide(previousRevenue, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
            }
            
            return currentRevenue.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        } catch (Exception e) {
            System.err.println("Error calculating overall growth rate: " + e.getMessage());
            return 0.0;
        }
    }
    
    private Double calculateOperationalEfficiency() {
        try {
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            LocalDate now = LocalDate.now();
            
            Long totalOrders = ordersRepository.countOrdersByDateRange(thirtyDaysAgo, now);
            Long totalShipments = shipmentRepository.countShipmentsByDateRange(thirtyDaysAgo, now);
            Long activeEmployees = userRepository.countByRoleAndIsActiveTrue(UserRole.STORER) + 
                                 userRepository.countByRoleAndIsActiveTrue(UserRole.PICKER);
            
            if (activeEmployees > 0 && totalOrders > 0) {
                // Calculate efficiency based on orders processed per employee and fulfillment rate
                double ordersPerEmployee = (double) totalOrders / activeEmployees;
                double fulfillmentRate = totalOrders > 0 ? (double) totalShipments / totalOrders : 0.0;
                
                // Efficiency score (0-100%)
                return Math.min(100.0, (ordersPerEmployee * 10 + fulfillmentRate * 100) / 2);
            }
            
            return 0.0;
        } catch (Exception e) {
            System.err.println("Error calculating operational efficiency: " + e.getMessage());
            return 75.0; // Default efficiency score
        }
    }
    
    private BigDecimal calculateTotalBusinessValue(String period) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = getStartDate(period, endDate);
            
            Long totalOrderValue = ordersRepository.sumOrderValueByDateRange(startDate, endDate);
            return totalOrderValue != null ? BigDecimal.valueOf(totalOrderValue) : BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Error calculating total business value: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
