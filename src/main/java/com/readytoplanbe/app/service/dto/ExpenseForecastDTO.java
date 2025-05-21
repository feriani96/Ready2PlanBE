package com.readytoplanbe.app.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.readytoplanbe.app.domain.ExpenseForecast} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExpenseForecastDTO implements Serializable {

    private String id;

    private String label;

    private Double monthlyAmount;

    private FinancialForecastDTO forecast;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(Double monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
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
        if (!(o instanceof ExpenseForecastDTO)) {
            return false;
        }

        ExpenseForecastDTO expenseForecastDTO = (ExpenseForecastDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, expenseForecastDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExpenseForecastDTO{" +
            "id='" + getId() + "'" +
            ", label='" + getLabel() + "'" +
            ", monthlyAmount=" + getMonthlyAmount() +
            ", forecast=" + getForecast() +
            "}";
    }
}
