package com.readytoplanbe.app.repository;

import com.readytoplanbe.app.domain.ExpenseForecast;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ExpenseForecast entity.
 */
@Repository
public interface ExpenseForecastRepository extends MongoRepository<ExpenseForecast, String> {
    @Query("{}")
    Page<ExpenseForecast> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<ExpenseForecast> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<ExpenseForecast> findOneWithEagerRelationships(String id);
    List<ExpenseForecast> findByForecastId(String forecastId);
}
