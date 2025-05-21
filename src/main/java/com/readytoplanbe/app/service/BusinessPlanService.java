package com.readytoplanbe.app.service;

import com.readytoplanbe.app.domain.BusinessPlan;
import com.readytoplanbe.app.domain.FinancialForecast;
import com.readytoplanbe.app.domain.ExpenseForecast;
import com.readytoplanbe.app.repository.BusinessPlanRepository;
import com.readytoplanbe.app.repository.ExpenseForecastRepository;
import com.readytoplanbe.app.repository.RevenueForecastRepository;
import com.readytoplanbe.app.repository.search.BusinessPlanSearchRepository;
import com.readytoplanbe.app.service.dto.BusinessPlanDTO;
import com.readytoplanbe.app.service.dto.FinancialSummaryDTO;
import com.readytoplanbe.app.service.mapper.BusinessPlanMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.readytoplanbe.app.domain.RevenueForecast;
import com.readytoplanbe.app.web.rest.errors.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link BusinessPlan}.
 */
@Service
public class BusinessPlanService {

    private final Logger log = LoggerFactory.getLogger(BusinessPlanService.class);

    private final BusinessPlanRepository businessPlanRepository;

    private final BusinessPlanMapper businessPlanMapper;

    private final BusinessPlanSearchRepository businessPlanSearchRepository;

    private final RevenueForecastRepository revenueForecastRepository;

    private final ExpenseForecastRepository  expenseForecastRepository;

    public BusinessPlanService(
        BusinessPlanRepository businessPlanRepository,
        BusinessPlanMapper businessPlanMapper,
        BusinessPlanSearchRepository businessPlanSearchRepository,
        RevenueForecastRepository revenueForecastRepository, ExpenseForecastRepository expenseForecastRepository) {
        this.businessPlanRepository = businessPlanRepository;
        this.businessPlanMapper = businessPlanMapper;
        this.businessPlanSearchRepository = businessPlanSearchRepository;
        this.revenueForecastRepository = revenueForecastRepository;
        this.expenseForecastRepository = expenseForecastRepository;

    }

    /**
     * Save a businessPlan.
     *
     * @param businessPlanDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessPlanDTO save(BusinessPlanDTO businessPlanDTO) {
        log.debug("Request to save BusinessPlan : {}", businessPlanDTO);
        BusinessPlan businessPlan = businessPlanMapper.toEntity(businessPlanDTO);
        businessPlan = businessPlanRepository.save(businessPlan);
        BusinessPlanDTO result = businessPlanMapper.toDto(businessPlan);
        businessPlanSearchRepository.index(businessPlan);
        return result;
    }

    /**
     * Update a businessPlan.
     *
     * @param businessPlanDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessPlanDTO update(BusinessPlanDTO businessPlanDTO) {
        log.debug("Request to update BusinessPlan : {}", businessPlanDTO);
        BusinessPlan businessPlan = businessPlanMapper.toEntity(businessPlanDTO);
        businessPlan = businessPlanRepository.save(businessPlan);
        BusinessPlanDTO result = businessPlanMapper.toDto(businessPlan);
        businessPlanSearchRepository.index(businessPlan);
        return result;
    }

    /**
     * Partially update a businessPlan.
     *
     * @param businessPlanDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BusinessPlanDTO> partialUpdate(BusinessPlanDTO businessPlanDTO) {
        log.debug("Request to partially update BusinessPlan : {}", businessPlanDTO);

        return businessPlanRepository
            .findById(businessPlanDTO.getId())
            .map(existingBusinessPlan -> {
                businessPlanMapper.partialUpdate(existingBusinessPlan, businessPlanDTO);

                return existingBusinessPlan;
            })
            .map(businessPlanRepository::save)
            .map(savedBusinessPlan -> {
                businessPlanSearchRepository.save(savedBusinessPlan);

                return savedBusinessPlan;
            })
            .map(businessPlanMapper::toDto);
    }

    /**
     * Get all the businessPlans.
     *
     * @return the list of entities.
     */
    public List<BusinessPlanDTO> findAll() {
        log.debug("Request to get all BusinessPlans");
        return businessPlanRepository.findAll().stream().map(businessPlanMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the businessPlans where Forecast is {@code null}.
     *  @return the list of entities.
     */

    public List<BusinessPlanDTO> findAllWhereForecastIsNull() {
        log.debug("Request to get all businessPlans where Forecast is null");
        return StreamSupport
            .stream(businessPlanRepository.findAll().spliterator(), false)
            .filter(businessPlan -> businessPlan.getForecast() == null)
            .map(businessPlanMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one businessPlan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<BusinessPlanDTO> findOne(String id) {
        log.debug("Request to get BusinessPlan : {}", id);
        return businessPlanRepository.findById(id).map(businessPlanMapper::toDto);
    }

    /**
     * Delete the businessPlan by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete BusinessPlan : {}", id);
        businessPlanRepository.deleteById(id);
        businessPlanSearchRepository.deleteById(id);
    }

    /**
     * Search for the businessPlan corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    public List<BusinessPlanDTO> search(String query) {
        log.debug("Request to search BusinessPlans for query {}", query);
        return StreamSupport
            .stream(businessPlanSearchRepository.search(query).spliterator(), false)
            .map(businessPlanMapper::toDto)
            .collect(Collectors.toList());
    }
    public FinancialSummaryDTO getFinancialSummary(String businessPlanId) {
        Optional<BusinessPlan> bp = businessPlanRepository.findById(businessPlanId);
        if (bp.isEmpty()) throw new NotFoundException("BusinessPlan not found");

        FinancialForecast forecast = bp.get().getForecast();
        if (forecast == null) return new FinancialSummaryDTO(0d, 0d, 0d);

        Double totalRevenue = revenueForecastRepository
            .findByForecastId(forecast.getId()) // forecast.getId() doit retourner un String
            .stream()
            .mapToDouble(RevenueForecast::getTotalRevenue) // pas getAmount mais getTotalRevenue
            .sum();

        Double totalExpense = expenseForecastRepository
            .findByForecastId(forecast.getId())
            .stream()
            .mapToDouble(ExpenseForecast::getMonthlyAmount)
            .sum();

        return new FinancialSummaryDTO(
            totalRevenue,
            totalExpense,
            totalRevenue - totalExpense
        );
    }


}
