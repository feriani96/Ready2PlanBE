package com.readytoplanbe.app.repository;

import com.readytoplanbe.app.domain.Entreprise;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Entreprise entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EntrepriseRepository extends MongoRepository<Entreprise, String> {}
