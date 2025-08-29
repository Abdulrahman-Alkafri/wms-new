package com.example.wmsnew.statistics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AnalyticsDto {
    
    public static class TimeSeriesData {
        private String period;
        private Long count;
        private BigDecimal totalValue;
        private LocalDate date;

        public TimeSeriesData() {}

        public TimeSeriesData(String period, Long count, BigDecimal totalValue, LocalDate date) {
            this.period = period;
            this.count = count;
            this.totalValue = totalValue;
            this.date = date;
        }

        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }

        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }

    public static class EmployeeRoleStats {
        private String role;
        private Long totalCount;
        private Long activeCount;
        private Double averageProductivity;

        public EmployeeRoleStats() {}

        public EmployeeRoleStats(String role, Long totalCount, Long activeCount, Double averageProductivity) {
            this.role = role;
            this.totalCount = totalCount;
            this.activeCount = activeCount;
            this.averageProductivity = averageProductivity;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }

        public Long getActiveCount() { return activeCount; }
        public void setActiveCount(Long activeCount) { this.activeCount = activeCount; }

        public Double getAverageProductivity() { return averageProductivity; }
        public void setAverageProductivity(Double averageProductivity) { this.averageProductivity = averageProductivity; }
    }

    public static class FinancialComparison {
        private String period;
        private BigDecimal orderRevenue;
        private BigDecimal shipmentCosts;
        private BigDecimal profit;
        private Double profitMargin;

        public FinancialComparison() {}

        public FinancialComparison(String period, BigDecimal orderRevenue, BigDecimal shipmentCosts, BigDecimal profit, Double profitMargin) {
            this.period = period;
            this.orderRevenue = orderRevenue;
            this.shipmentCosts = shipmentCosts;
            this.profit = profit;
            this.profitMargin = profitMargin;
        }

        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public BigDecimal getOrderRevenue() { return orderRevenue; }
        public void setOrderRevenue(BigDecimal orderRevenue) { this.orderRevenue = orderRevenue; }

        public BigDecimal getShipmentCosts() { return shipmentCosts; }
        public void setShipmentCosts(BigDecimal shipmentCosts) { this.shipmentCosts = shipmentCosts; }

        public BigDecimal getProfit() { return profit; }
        public void setProfit(BigDecimal profit) { this.profit = profit; }

        public Double getProfitMargin() { return profitMargin; }
        public void setProfitMargin(Double profitMargin) { this.profitMargin = profitMargin; }
    }

    public static class AnalyticsResponse {
        private List<TimeSeriesData> ordersTimeSeries;
        private List<TimeSeriesData> shipmentsTimeSeries;
        private List<EmployeeRoleStats> employeeStats;
        private List<FinancialComparison> financialComparison;

        public AnalyticsResponse() {}

        public AnalyticsResponse(List<TimeSeriesData> ordersTimeSeries, List<TimeSeriesData> shipmentsTimeSeries, 
                               List<EmployeeRoleStats> employeeStats, List<FinancialComparison> financialComparison) {
            this.ordersTimeSeries = ordersTimeSeries;
            this.shipmentsTimeSeries = shipmentsTimeSeries;
            this.employeeStats = employeeStats;
            this.financialComparison = financialComparison;
        }

        public List<TimeSeriesData> getOrdersTimeSeries() { return ordersTimeSeries; }
        public void setOrdersTimeSeries(List<TimeSeriesData> ordersTimeSeries) { this.ordersTimeSeries = ordersTimeSeries; }

        public List<TimeSeriesData> getShipmentsTimeSeries() { return shipmentsTimeSeries; }
        public void setShipmentsTimeSeries(List<TimeSeriesData> shipmentsTimeSeries) { this.shipmentsTimeSeries = shipmentsTimeSeries; }

        public List<EmployeeRoleStats> getEmployeeStats() { return employeeStats; }
        public void setEmployeeStats(List<EmployeeRoleStats> employeeStats) { this.employeeStats = employeeStats; }

        public List<FinancialComparison> getFinancialComparison() { return financialComparison; }
        public void setFinancialComparison(List<FinancialComparison> financialComparison) { this.financialComparison = financialComparison; }
    }
}