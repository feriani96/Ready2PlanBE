package com.readytoplanbe.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A FinancialForecast.
 */
@Document(collection = "financial_forecast")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "financialforecast")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FinancialForecast implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("start_date")
    private Long startDate;

    @Field("duration_in_months")
    private Integer durationInMonths;

    @DBRef
    @Field("businessPlan")
    private BusinessPlan businessPlan;

    @DBRef
    @Field("revenues")
    @JsonIgnoreProperties(value = { "product", "forecast" }, allowSetters = true)
    private Set<RevenueForecast> revenues = new HashSet<>();

    @DBRef
    @Field("expenses")
    @JsonIgnoreProperties(value = { "forecast" }, allowSetters = true)
    private Set<ExpenseForecast> expenses = new HashSet<>();


    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public FinancialForecast id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStartDate() {
        return this.startDate;
    }

    public FinancialForecast startDate(Long startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Integer getDurationInMonths() {
        return this.durationInMonths;
    }

    public FinancialForecast durationInMonths(Integer durationInMonths) {
        this.setDurationInMonths(durationInMonths);
        return this;
    }

    public void setDurationInMonths(Integer durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public BusinessPlan getBusinessPlan() {
        return this.businessPlan;
    }

    public void setBusinessPlan(BusinessPlan businessPlan) {
        this.businessPlan = businessPlan;
    }

    public FinancialForecast businessPlan(BusinessPlan businessPlan) {
        this.setBusinessPlan(businessPlan);
        return this;
    }

    public Set<RevenueForecast> getRevenues() {
        return this.revenues;
    }

    public void setRevenues(Set<RevenueForecast> revenueForecasts) {
        if (this.revenues != null) {
            this.revenues.forEach(i -> i.setForecast(null));
        }
        if (revenueForecasts != null) {
            revenueForecasts.forEach(i -> i.setForecast(this));
        }
        this.revenues = revenueForecasts;
    }

    public FinancialForecast revenues(Set<RevenueForecast> revenueForecasts) {
        this.setRevenues(revenueForecasts);
        return this;
    }

    public FinancialForecast addRevenues(RevenueForecast revenueForecast) {
        this.revenues.add(revenueForecast);
        revenueForecast.setForecast(this);
        return this;
    }

    public FinancialForecast removeRevenues(RevenueForecast revenueForecast) {
        this.revenues.remove(revenueForecast);
        revenueForecast.setForecast(null);
        return this;
    }

    public Set<ExpenseForecast> getExpenses() {
        return this.expenses;
    }

    public void setExpenses(Set<ExpenseForecast> expenseForecasts) {
        if (this.expenses != null) {
            this.expenses.forEach(i -> i.setForecast(null));
        }
        if (expenseForecasts != null) {
            expenseForecasts.forEach(i -> i.setForecast(this));
        }
        this.expenses = expenseForecasts;
    }

    public FinancialForecast expenses(Set<ExpenseForecast> expenseForecasts) {
        this.setExpenses(expenseForecasts);
        return this;
    }

    public FinancialForecast addExpenses(ExpenseForecast expenseForecast) {
        this.expenses.add(expenseForecast);
        expenseForecast.setForecast(this);
        return this;
    }

    public FinancialForecast removeExpenses(ExpenseForecast expenseForecast) {
        this.expenses.remove(expenseForecast);
        expenseForecast.setForecast(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinancialForecast)) {
            return false;
        }
        return id != null && id.equals(((FinancialForecast) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinancialForecast{" +
            "id=" + getId() +
            ", startDate=" + getStartDate() +
            ", durationInMonths=" + getDurationInMonths() +
            "}";
    }
}
