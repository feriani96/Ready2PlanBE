package com.readytoplanbe.app.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.readytoplanbe.app.domain.BusinessPlan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BusinessPlanDTO implements Serializable {

    private String id;
    private String name;
    private String description;
    private Long creationDate; // wrapper Long pour la cohérence
    private String entrepreneurName; // correction typo

    private Set<ProductOrServiceDTO> products;
    private FinancialForecastDTO forecast;

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

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getEntrepreneurName() {
        return entrepreneurName;
    }

    public void setEntrepreneurName(String entrepreneurName) {
        this.entrepreneurName = entrepreneurName;
    }

    public Set<ProductOrServiceDTO> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductOrServiceDTO> products) {
        this.products = products;
    }

    public FinancialForecastDTO getForecast() {
        return forecast;
    }

    public void setForecast(FinancialForecastDTO forecast) {
        this.forecast = forecast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessPlanDTO)) return false;
        BusinessPlanDTO that = (BusinessPlanDTO) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BusinessPlanDTO{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", creationDate=" + creationDate +
            ", entrepreneurName='" + entrepreneurName + '\'' +
            '}';
    }
}
