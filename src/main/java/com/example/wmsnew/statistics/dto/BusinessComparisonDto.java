package com.example.wmsnew.statistics.dto;

import java.math.BigDecimal;
import java.util.List;

public class BusinessComparisonDto {
    
    public static class OrderShipmentComparison {
        private String period;
        private Long ordersCount;
        private Long shipmentsCount;
        private BigDecimal ordersRevenue;
        private BigDecimal shipmentsCosts;
        private Double fulfillmentRate; // shipments/orders ratio
        private Double profitabilityScore;
        private Long pendingOrders;
        private Long delayedShipments;

        public OrderShipmentComparison() {}

        public OrderShipmentComparison(String period, Long ordersCount, Long shipmentsCount, 
                                     BigDecimal ordersRevenue, BigDecimal shipmentsCosts, 
                                     Double fulfillmentRate, Double profitabilityScore,
                                     Long pendingOrders, Long delayedShipments) {
            this.period = period;
            this.ordersCount = ordersCount;
            this.shipmentsCount = shipmentsCount;
            this.ordersRevenue = ordersRevenue;
            this.shipmentsCosts = shipmentsCosts;
            this.fulfillmentRate = fulfillmentRate;
            this.profitabilityScore = profitabilityScore;
            this.pendingOrders = pendingOrders;
            this.delayedShipments = delayedShipments;
        }

        // Getters and Setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public Long getOrdersCount() { return ordersCount; }
        public void setOrdersCount(Long ordersCount) { this.ordersCount = ordersCount; }

        public Long getShipmentsCount() { return shipmentsCount; }
        public void setShipmentsCount(Long shipmentsCount) { this.shipmentsCount = shipmentsCount; }

        public BigDecimal getOrdersRevenue() { return ordersRevenue; }
        public void setOrdersRevenue(BigDecimal ordersRevenue) { this.ordersRevenue = ordersRevenue; }

        public BigDecimal getShipmentsCosts() { return shipmentsCosts; }
        public void setShipmentsCosts(BigDecimal shipmentsCosts) { this.shipmentsCosts = shipmentsCosts; }

        public Double getFulfillmentRate() { return fulfillmentRate; }
        public void setFulfillmentRate(Double fulfillmentRate) { this.fulfillmentRate = fulfillmentRate; }

        public Double getProfitabilityScore() { return profitabilityScore; }
        public void setProfitabilityScore(Double profitabilityScore) { this.profitabilityScore = profitabilityScore; }

        public Long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(Long pendingOrders) { this.pendingOrders = pendingOrders; }

        public Long getDelayedShipments() { return delayedShipments; }
        public void setDelayedShipments(Long delayedShipments) { this.delayedShipments = delayedShipments; }
    }
    
    public static class PaymentAnalytics {
        private String period;
        private BigDecimal totalPaymentsReceived;
        private BigDecimal pendingPayments;
        private Double paymentSuccessRate;
        private BigDecimal averagePaymentValue;
        private Long totalTransactions;
        private Long failedTransactions;

        public PaymentAnalytics() {}

        public PaymentAnalytics(String period, BigDecimal totalPaymentsReceived, BigDecimal pendingPayments,
                              Double paymentSuccessRate, BigDecimal averagePaymentValue, 
                              Long totalTransactions, Long failedTransactions) {
            this.period = period;
            this.totalPaymentsReceived = totalPaymentsReceived;
            this.pendingPayments = pendingPayments;
            this.paymentSuccessRate = paymentSuccessRate;
            this.averagePaymentValue = averagePaymentValue;
            this.totalTransactions = totalTransactions;
            this.failedTransactions = failedTransactions;
        }

        // Getters and Setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public BigDecimal getTotalPaymentsReceived() { return totalPaymentsReceived; }
        public void setTotalPaymentsReceived(BigDecimal totalPaymentsReceived) { this.totalPaymentsReceived = totalPaymentsReceived; }

        public BigDecimal getPendingPayments() { return pendingPayments; }
        public void setPendingPayments(BigDecimal pendingPayments) { this.pendingPayments = pendingPayments; }

        public Double getPaymentSuccessRate() { return paymentSuccessRate; }
        public void setPaymentSuccessRate(Double paymentSuccessRate) { this.paymentSuccessRate = paymentSuccessRate; }

        public BigDecimal getAveragePaymentValue() { return averagePaymentValue; }
        public void setAveragePaymentValue(BigDecimal averagePaymentValue) { this.averagePaymentValue = averagePaymentValue; }

        public Long getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

        public Long getFailedTransactions() { return failedTransactions; }
        public void setFailedTransactions(Long failedTransactions) { this.failedTransactions = failedTransactions; }
    }
    
    public static class BusinessComparisonResponse {
        private List<OrderShipmentComparison> orderShipmentComparison;
        private List<PaymentAnalytics> paymentAnalytics;
        private Double overallGrowthRate;
        private Double operationalEfficiency;
        private BigDecimal totalBusinessValue;

        public BusinessComparisonResponse() {}

        public BusinessComparisonResponse(List<OrderShipmentComparison> orderShipmentComparison,
                                        List<PaymentAnalytics> paymentAnalytics,
                                        Double overallGrowthRate, Double operationalEfficiency,
                                        BigDecimal totalBusinessValue) {
            this.orderShipmentComparison = orderShipmentComparison;
            this.paymentAnalytics = paymentAnalytics;
            this.overallGrowthRate = overallGrowthRate;
            this.operationalEfficiency = operationalEfficiency;
            this.totalBusinessValue = totalBusinessValue;
        }

        // Getters and Setters
        public List<OrderShipmentComparison> getOrderShipmentComparison() { return orderShipmentComparison; }
        public void setOrderShipmentComparison(List<OrderShipmentComparison> orderShipmentComparison) { this.orderShipmentComparison = orderShipmentComparison; }

        public List<PaymentAnalytics> getPaymentAnalytics() { return paymentAnalytics; }
        public void setPaymentAnalytics(List<PaymentAnalytics> paymentAnalytics) { this.paymentAnalytics = paymentAnalytics; }

        public Double getOverallGrowthRate() { return overallGrowthRate; }
        public void setOverallGrowthRate(Double overallGrowthRate) { this.overallGrowthRate = overallGrowthRate; }

        public Double getOperationalEfficiency() { return operationalEfficiency; }
        public void setOperationalEfficiency(Double operationalEfficiency) { this.operationalEfficiency = operationalEfficiency; }

        public BigDecimal getTotalBusinessValue() { return totalBusinessValue; }
        public void setTotalBusinessValue(BigDecimal totalBusinessValue) { this.totalBusinessValue = totalBusinessValue; }
    }
}
