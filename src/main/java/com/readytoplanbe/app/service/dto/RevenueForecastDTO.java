package com.readytoplanbe.app.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.readytoplanbe.app.domain.RevenueForecast} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RevenueForecastDTO implements Serializable {

    private String id;

    private Integer month;

    private Integer year;

    private Double unitsSold;

    private Double totalRevenue;

    private ProductOrServiceDTO product;

    private FinancialForecastDTO forecast;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Double unitsSold) {
        this.unitsSold = unitsSold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public ProductOrServiceDTO getProduct() {
        return product;
    }

    public void setProduct(ProductOrServiceDTO product) {
        this.product = product;
    }

    public FinancialForecastDTO getForecast() {
        return forecast;
    }

    public void setForecast(FinancialForecastDTO forecast) {
        this.forecast = forecast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RevenueForecastDTO)) {
            return false;
        }

        RevenueForecastDTO revenueForecastDTO = (RevenueForecastDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, revenueForecastDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RevenueForecastDTO{" +
            "id='" + getId() + "'" +
            ", month=" + getMonth() +
            ", year=" + getYear() +
            ", unitsSold=" + getUnitsSold() +
            ", totalRevenue=" + getTotalRevenue() +
            ", product=" + getProduct() +
            ", forecast=" + getForecast() +
            "}";
    }
}
