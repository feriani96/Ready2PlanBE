package com.readytoplanbe.app.repository;

import com.readytoplanbe.app.domain.BusinessPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for the BusinessPlan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BusinessPlanRepository extends MongoRepository<BusinessPlan, String> {
    Optional<BusinessPlan> findById(Long businessPlanId);
}
