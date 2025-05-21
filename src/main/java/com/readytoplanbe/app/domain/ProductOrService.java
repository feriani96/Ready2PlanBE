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
 * A ProductOrService.
 */
@Document(collection = "product_or_service")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productorservice")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductOrService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("unit_price")
    private Double unitPrice;

    @Field("estimated_monthly_sales")
    private Integer estimatedMonthlySales;

    @Field("duration_in_months")
    private Integer durationInMonths;

    @DBRef
    @Field("revenueForecasts")
    @JsonIgnoreProperties(value = { "product", "forecast" }, allowSetters = true)
    private Set<RevenueForecast> revenueForecasts = new HashSet<>();

    @DBRef
    @Field("businessPlan")
    @JsonIgnoreProperties(value = { "products", "forecast" }, allowSetters = true)
    private BusinessPlan businessPlan;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ProductOrService id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ProductOrService name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public ProductOrService description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getUnitPrice() {
        return this.unitPrice;
    }

    public ProductOrService unitPrice(Double unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getEstimatedMonthlySales() {
        return this.estimatedMonthlySales;
    }

    public ProductOrService estimatedMonthlySales(Integer estimatedMonthlySales) {
        this.setEstimatedMonthlySales(estimatedMonthlySales);
        return this;
    }

    public void setEstimatedMonthlySales(Integer estimatedMonthlySales) {
        this.estimatedMonthlySales = estimatedMonthlySales;
    }

    public Integer getDurationInMonths() {
        return this.durationInMonths;
    }

    public ProductOrService durationInMonths(Integer durationInMonths) {
        this.setDurationInMonths(durationInMonths);
        return this;
    }

    public void setDurationInMonths(Integer durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public Set<RevenueForecast> getRevenueForecasts() {
        return this.revenueForecasts;
    }

    public void setRevenueForecasts(Set<RevenueForecast> revenueForecasts) {
        if (this.revenueForecasts != null) {
            this.revenueForecasts.forEach(i -> i.setProduct(null));
        }
        if (revenueForecasts != null) {
            revenueForecasts.forEach(i -> i.setProduct(this));
        }
        this.revenueForecasts = revenueForecasts;
    }

    public ProductOrService revenueForecasts(Set<RevenueForecast> revenueForecasts) {
        this.setRevenueForecasts(revenueForecasts);
        return this;
    }

    public ProductOrService addRevenueForecasts(RevenueForecast revenueForecast) {
        this.revenueForecasts.add(revenueForecast);
        revenueForecast.setProduct(this);
        return this;
    }

    public ProductOrService removeRevenueForecasts(RevenueForecast revenueForecast) {
        this.revenueForecasts.remove(revenueForecast);
        revenueForecast.setProduct(null);
        return this;
    }

    public BusinessPlan getBusinessPlan() {
        return this.businessPlan;
    }

    public void setBusinessPlan(BusinessPlan businessPlan) {
        this.businessPlan = businessPlan;
    }

    public ProductOrService businessPlan(BusinessPlan businessPlan) {
        this.setBusinessPlan(businessPlan);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductOrService)) {
            return false;
        }
        return id != null && id.equals(((ProductOrService) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductOrService{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", unitPrice=" + getUnitPrice() +
            ", estimatedMonthlySales=" + getEstimatedMonthlySales() +
            ", durationInMonths=" + getDurationInMonths() +
            "}";
    }
}
