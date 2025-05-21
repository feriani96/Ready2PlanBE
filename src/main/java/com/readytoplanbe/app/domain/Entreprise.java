package com.readytoplanbe.app.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Entreprise.
 */
@Document(collection = "entreprise")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "entreprise")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Entreprise implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("nom_etp")
    private String nom_etp;

    @Field("pays")
    private String pays;

    @Field("telephone")
    private Long telephone;

    @Field("description")
    private String description;

    @Field("devise_size")
    private Long deviseSize;

    @Field("devise")
    private String devise;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Entreprise id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom_etp() {
        return this.nom_etp;
    }

    public Entreprise nom_etp(String nom_etp) {
        this.setNom_etp(nom_etp);
        return this;
    }

    public void setNom_etp(String nom_etp) {
        this.nom_etp = nom_etp;
    }

    public String getPays() {
        return this.pays;
    }

    public Entreprise pays(String pays) {
        this.setPays(pays);
        return this;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Long getTelephone() {
        return this.telephone;
    }

    public Entreprise telephone(Long telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(Long telephone) {
        this.telephone = telephone;
    }

    public String getDescription() {
        return this.description;
    }

    public Entreprise description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDeviseSize() {
        return this.deviseSize;
    }

    public Entreprise deviseSize(Long deviseSize) {
        this.setDeviseSize(deviseSize);
        return this;
    }

    public void setDeviseSize(Long deviseSize) {
        this.deviseSize = deviseSize;
    }

    public String getDevise() {
        return this.devise;
    }

    public Entreprise devise(String devise) {
        this.setDevise(devise);
        return this;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entreprise)) {
            return false;
        }
        return id != null && id.equals(((Entreprise) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Entreprise{" +
            "id=" + getId() +
            ", nom_etp='" + getNom_etp() + "'" +
            ", pays='" + getPays() + "'" +
            ", telephone=" + getTelephone() +
            ", description='" + getDescription() + "'" +
            ", deviseSize=" + getDeviseSize() +
            ", devise='" + getDevise() + "'" +
            "}";
    }
}
