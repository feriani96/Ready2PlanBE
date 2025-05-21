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
 * A BusinessPlan.
 */
@Document(collection = "business_plan")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "businessplan")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BusinessPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("creation_date")
    private Long creationDate;

    @Field("entropreneur_name")
    private String entropreneurName;

    @DBRef
    @Field("products")
    @JsonIgnoreProperties(value = { "revenueForecasts", "businessPlan" }, allowSetters = true)
    private Set<ProductOrService> products = new HashSet<>();

    @DBRef
    @JsonIgnoreProperties(value = { "businessPlan", "revenues", "expenses" }, allowSetters = true)
    private FinancialForecast forecast;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public BusinessPlan id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public BusinessPlan name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public BusinessPlan description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreationDate() {
        return this.creationDate;
    }

    public BusinessPlan creationDate(Long creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getEntropreneurName() {
        return this.entropreneurName;
    }

    public BusinessPlan entropreneurName(String entropreneurName) {
        this.setEntropreneurName(entropreneurName);
        return this;
    }

    public void setEntropreneurName(String entropreneurName) {
        this.entropreneurName = entropreneurName;
    }

    public Set<ProductOrService> getProducts() {
        return this.products;
    }

    public void setProducts(Set<ProductOrService> productOrServices) {
        if (this.products != null) {
            this.products.forEach(i -> i.setBusinessPlan(null));
        }
        if (productOrServices != null) {
            productOrServices.forEach(i -> i.setBusinessPlan(this));
        }
        this.products = productOrServices;
    }

    public BusinessPlan products(Set<ProductOrService> productOrServices) {
        this.setProducts(productOrServices);
        return this;
    }

    public BusinessPlan addProducts(ProductOrService productOrService) {
        this.products.add(productOrService);
        productOrService.setBusinessPlan(this);
        return this;
    }

    public BusinessPlan removeProducts(ProductOrService productOrService) {
        this.products.remove(productOrService);
        productOrService.setBusinessPlan(null);
        return this;
    }

    public FinancialForecast getForecast() {
        return this.forecast;
    }

    public void setForecast(FinancialForecast financialForecast) {
        if (this.forecast != null) {
            this.forecast.setBusinessPlan(null);
        }
        if (financialForecast != null) {
            financialForecast.setBusinessPlan(this);
        }
        this.forecast = financialForecast;
    }

    public BusinessPlan forecast(FinancialForecast financialForecast) {
        this.setForecast(financialForecast);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BusinessPlan)) {
            return false;
        }
        return id != null && id.equals(((BusinessPlan) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BusinessPlan{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", creationDate=" + getCreationDate() +
            ", entropreneurName='" + getEntropreneurName() + "'" +
            "}";
    }
}
