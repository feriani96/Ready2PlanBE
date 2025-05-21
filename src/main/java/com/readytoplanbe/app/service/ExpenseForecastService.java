package com.readytoplanbe.app.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.readytoplanbe.app.domain.ExpenseForecast;
import com.readytoplanbe.app.repository.ExpenseForecastRepository;
import com.readytoplanbe.app.repository.search.ExpenseForecastSearchRepository;
import com.readytoplanbe.app.service.dto.ExpenseForecastDTO;
import com.readytoplanbe.app.service.mapper.ExpenseForecastMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link ExpenseForecast}.
 */
@Service
public class ExpenseForecastService {

    private final Logger log = LoggerFactory.getLogger(ExpenseForecastService.class);

    private final ExpenseForecastRepository expenseForecastRepository;

    private final ExpenseForecastMapper expenseForecastMapper;

    private final ExpenseForecastSearchRepository expenseForecastSearchRepository;

    public ExpenseForecastService(
        ExpenseForecastRepository expenseForecastRepository,
        ExpenseForecastMapper expenseForecastMapper,
        ExpenseForecastSearchRepository expenseForecastSearchRepository
    ) {
        this.expenseForecastRepository = expenseForecastRepository;
        this.expenseForecastMapper = expenseForecastMapper;
        this.expenseForecastSearchRepository = expenseForecastSearchRepository;
    }

    /**
     * Save a expenseForecast.
     *
     * @param expenseForecastDTO the entity to save.
     * @return the persisted entity.
     */
    public ExpenseForecastDTO save(ExpenseForecastDTO expenseForecastDTO) {
        log.debug("Request to save ExpenseForecast : {}", expenseForecastDTO);
        ExpenseForecast expenseForecast = expenseForecastMapper.toEntity(expenseForecastDTO);
        expenseForecast = expenseForecastRepository.save(expenseForecast);
        ExpenseForecastDTO result = expenseForecastMapper.toDto(expenseForecast);
        expenseForecastSearchRepository.index(expenseForecast);
        return result;
    }

    /**
     * Update a expenseForecast.
     *
     * @param expenseForecastDTO the entity to save.
     * @return the persisted entity.
     */
    public ExpenseForecastDTO update(ExpenseForecastDTO expenseForecastDTO) {
        log.debug("Request to update ExpenseForecast : {}", expenseForecastDTO);
        ExpenseForecast expenseForecast = expenseForecastMapper.toEntity(expenseForecastDTO);
        expenseForecast = expenseForecastRepository.save(expenseForecast);
        ExpenseForecastDTO result = expenseForecastMapper.toDto(expenseForecast);
        expenseForecastSearchRepository.index(expenseForecast);
        return result;
    }

    /**
     * Partially update a expenseForecast.
     *
     * @param expenseForecastDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ExpenseForecastDTO> partialUpdate(ExpenseForecastDTO expenseForecastDTO) {
        log.debug("Request to partially update ExpenseForecast : {}", expenseForecastDTO);

        return expenseForecastRepository
            .findById(expenseForecastDTO.getId())
            .map(existingExpenseForecast -> {
                expenseForecastMapper.partialUpdate(existingExpenseForecast, expenseForecastDTO);

                return existingExpenseForecast;
            })
            .map(expenseForecastRepository::save)
            .map(savedExpenseForecast -> {
                expenseForecastSearchRepository.save(savedExpenseForecast);

                return savedExpenseForecast;
            })
            .map(expenseForecastMapper::toDto);
    }

    /**
     * Get all the expenseForecasts.
     *
     * @return the list of entities.
     */
    public List<ExpenseForecastDTO> findAll() {
        log.debug("Request to get all ExpenseForecasts");
        return expenseForecastRepository
            .findAll()
            .stream()
            .map(expenseForecastMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the expenseForecasts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ExpenseForecastDTO> findAllWithEagerRelationships(Pageable pageable) {
        return expenseForecastRepository.findAllWithEagerRelationships(pageable).map(expenseForecastMapper::toDto);
    }

    /**
     * Get one expenseForecast by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ExpenseForecastDTO> findOne(String id) {
        log.debug("Request to get ExpenseForecast : {}", id);
        return expenseForecastRepository.findOneWithEagerRelationships(id).map(expenseForecastMapper::toDto);
    }

    /**
     * Delete the expenseForecast by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete ExpenseForecast : {}", id);
        expenseForecastRepository.deleteById(id);
        expenseForecastSearchRepository.deleteById(id);
    }

    /**
     * Search for the expenseForecast corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    public List<ExpenseForecastDTO> search(String query) {
        log.debug("Request to search ExpenseForecasts for query {}", query);
        return StreamSupport
            .stream(expenseForecastSearchRepository.search(query).spliterator(), false)
            .map(expenseForecastMapper::toDto)
            .collect(Collectors.toList());
    }
}
