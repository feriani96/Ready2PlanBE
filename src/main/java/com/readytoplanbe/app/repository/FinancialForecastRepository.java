package com.readytoplanbe.app.repository;

import com.readytoplanbe.app.domain.FinancialForecast;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the FinancialForecast entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FinancialForecastRepository extends MongoRepository<FinancialForecast, String> {}
