package com.readytoplanbe.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ExpenseForecast.
 */
@Document(collection = "expense_forecast")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "expenseforecast")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExpenseForecast implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("label")
    private String label;

    @Field("monthly_amount")
    private Double monthlyAmount;

    @DBRef
    @Field("forecast")
    @JsonIgnoreProperties(value = { "businessPlan", "revenues", "expenses" }, allowSetters = true)
    private FinancialForecast forecast;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ExpenseForecast id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public ExpenseForecast label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getMonthlyAmount() {
        return this.monthlyAmount;
    }

    public ExpenseForecast monthlyAmount(Double monthlyAmount) {
        this.setMonthlyAmount(monthlyAmount);
        return this;
    }

    public void setMonthlyAmount(Double monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public FinancialForecast getForecast() {
        return this.forecast;
    }

    public void setForecast(FinancialForecast financialForecast) {
        this.forecast = financialForecast;
    }

    public ExpenseForecast forecast(FinancialForecast financialForecast) {
        this.setForecast(financialForecast);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExpenseForecast)) {
            return false;
        }
        return id != null && id.equals(((ExpenseForecast) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExpenseForecast{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", monthlyAmount=" + getMonthlyAmount() +
            "}";
    }
}
