package com.readytoplanbe.app.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.readytoplanbe.app.domain.ProductOrService} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductOrServiceDTO implements Serializable {

    private String id;

    private String name;

    private String description;

    private Double unitPrice;

    private Integer estimatedMonthlySales;

    private Integer durationInMonths;

    private BusinessPlanDTO businessPlan;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getEstimatedMonthlySales() {
        return estimatedMonthlySales;
    }

    public void setEstimatedMonthlySales(Integer estimatedMonthlySales) {
        this.estimatedMonthlySales = estimatedMonthlySales;
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
        if (!(o instanceof ProductOrServiceDTO)) {
            return false;
        }

        ProductOrServiceDTO productOrServiceDTO = (ProductOrServiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productOrServiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductOrServiceDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", unitPrice=" + getUnitPrice() +
            ", estimatedMonthlySales=" + getEstimatedMonthlySales() +
            ", durationInMonths=" + getDurationInMonths() +
            ", businessPlan=" + getBusinessPlan() +
            "}";
    }
}
