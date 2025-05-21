package com.readytoplanbe.app.repository;

import com.readytoplanbe.app.domain.RevenueForecast;
import java.util.List;
import java.util.Optional;

import io.micrometer.core.instrument.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the RevenueForecast entity.
 */
@Repository
public interface RevenueForecastRepository extends MongoRepository<RevenueForecast, String> {
    @Query("{}")
    Page<RevenueForecast> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<RevenueForecast> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<RevenueForecast> findOneWithEagerRelationships(String id);

    List<RevenueForecast> findByForecastId(String forecastId);

    // (Optionnel) Pour récupérer tous les RevenueForecasts d'un BusinessPlan via la relation imbriquée
    @Query("SELECT rf FROM RevenueForecast rf WHERE rf.financialForecast.businessPlan.id = :businessPlanId")
    List<RevenueForecast> findByBusinessPlanId(@Param("businessPlanId") Long businessPlanId);


}
