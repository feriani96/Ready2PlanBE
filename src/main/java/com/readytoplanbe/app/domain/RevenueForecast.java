package com.readytoplanbe.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A RevenueForecast.
 */
@Document(collection = "revenue_forecast")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "revenueforecast")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RevenueForecast implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("month")
    private Integer month;

    @Field("year")
    private Integer year;

    @Field("units_sold")
    private Double unitsSold;

    @Field("total_revenue")
    private Double totalRevenue;

    @DBRef
    @Field("product")
    @JsonIgnoreProperties(value = { "revenueForecasts", "businessPlan" }, allowSetters = true)
    private ProductOrService product;

    @DBRef
    @Field("forecast")
    @JsonIgnoreProperties(value = { "businessPlan", "revenues", "expenses" }, allowSetters = true)
    private FinancialForecast forecast;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public RevenueForecast id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMonth() {
        return this.month;
    }

    public RevenueForecast month(Integer month) {
        this.setMonth(month);
        return this;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return this.year;
    }

    public RevenueForecast year(Integer year) {
        this.setYear(year);
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getUnitsSold() {
        return this.unitsSold;
    }

    public RevenueForecast unitsSold(Double unitsSold) {
        this.setUnitsSold(unitsSold);
        return this;
    }

    public void setUnitsSold(Double unitsSold) {
        this.unitsSold = unitsSold;
    }

    public Double getTotalRevenue() {
        return this.totalRevenue;
    }

    public RevenueForecast totalRevenue(Double totalRevenue) {
        this.setTotalRevenue(totalRevenue);
        return this;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public ProductOrService getProduct() {
        return this.product;
    }

    public void setProduct(ProductOrService productOrService) {
        this.product = productOrService;
    }

    public RevenueForecast product(ProductOrService productOrService) {
        this.setProduct(productOrService);
        return this;
    }

    public FinancialForecast getForecast() {
        return this.forecast;
    }

    public void setForecast(FinancialForecast financialForecast) {
        this.forecast = financialForecast;
    }

    public RevenueForecast forecast(FinancialForecast financialForecast) {
        this.setForecast(financialForecast);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RevenueForecast)) {
            return false;
        }
        return id != null && id.equals(((RevenueForecast) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RevenueForecast{" +
            "id=" + getId() +
            ", month=" + getMonth() +
            ", year=" + getYear() +
            ", unitsSold=" + getUnitsSold() +
            ", totalRevenue=" + getTotalRevenue() +
            "}";
    }
}
