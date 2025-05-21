package com.readytoplanbe.app.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.readytoplanbe.app.domain.FinancialForecast} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FinancialForecastDTO implements Serializable {

    private String id;

    private Long startDate;

    private Integer durationInMonths;

    private BusinessPlanDTO businessPlan;
    private Set<RevenueForecastDTO> revenues;
    private Set<ExpenseForecastDTO> expenses;

    public Set<RevenueForecastDTO> getRevenues() {
        return revenues;
    }

    public void setRevenues(Set<RevenueForecastDTO> revenues) {
        this.revenues = revenues;
    }

    public Set<ExpenseForecastDTO> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<ExpenseForecastDTO> expenses) {
        this.expenses = expenses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Integer durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public BusinessPlanDTO getBusinessPlan() {
        return businessPlan;
    }

    public void setBusinessPlan(BusinessPlanDTO businessPlan) {
        this.businessPlan = businessPlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinancialForecastDTO)) {
            return false;
        }

        FinancialForecastDTO financialForecastDTO = (FinancialForecastDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, financialForecastDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FinancialForecastDTO{" +
            "id='" + getId() + "'" +
            ", startDate=" + getStartDate() +
            ", durationInMonths=" + getDurationInMonths() +
            ", businessPlan=" + getBusinessPlan() +
            "}";
    }
}
