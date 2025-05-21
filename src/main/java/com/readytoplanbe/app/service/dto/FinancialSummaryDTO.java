package com.readytoplanbe.app.service.dto;

public class FinancialSummaryDTO {
    private Double totalRevenue;
    private Double totalExpense;
    private Double profit;

    public FinancialSummaryDTO(Double totalRevenue, Double totalExpense, Double profit) {
        this.totalRevenue = totalRevenue;
        this.totalExpense = totalExpense;
        this.profit = profit;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }
}
