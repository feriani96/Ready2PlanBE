package com.readytoplanbe.app.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.readytoplanbe.app.domain.FinancialForecast;
import com.readytoplanbe.app.repository.FinancialForecastRepository;
import com.readytoplanbe.app.repository.search.FinancialForecastSearchRepository;
import com.readytoplanbe.app.service.dto.FinancialForecastDTO;
import com.readytoplanbe.app.service.mapper.FinancialForecastMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link FinancialForecast}.
 */
@Service
public class FinancialForecastService {

    private final Logger log = LoggerFactory.getLogger(FinancialForecastService.class);

    private final FinancialForecastRepository financialForecastRepository;

    private final FinancialForecastMapper financialForecastMapper;

    private final FinancialForecastSearchRepository financialForecastSearchRepository;

    public FinancialForecastService(
        FinancialForecastRepository financialForecastRepository,
        FinancialForecastMapper financialForecastMapper,
        FinancialForecastSearchRepository financialForecastSearchRepository
    ) {
        this.financialForecastRepository = financialForecastRepository;
        this.financialForecastMapper = financialForecastMapper;
        this.financialForecastSearchRepository = financialForecastSearchRepository;
    }

    /**
     * Save a financialForecast.
     *
     * @param financialForecastDTO the entity to save.
     * @return the persisted entity.
     */
    public FinancialForecastDTO save(FinancialForecastDTO financialForecastDTO) {
        log.debug("Request to save FinancialForecast : {}", financialForecastDTO);
        FinancialForecast financialForecast = financialForecastMapper.toEntity(financialForecastDTO);
        financialForecast = financialForecastRepository.save(financialForecast);
        FinancialForecastDTO result = financialForecastMapper.toDto(financialForecast);
        financialForecastSearchRepository.index(financialForecast);
        return result;
    }

    /**
     * Update a financialForecast.
     *
     * @param financialForecastDTO the entity to save.
     * @return the persisted entity.
     */
    public FinancialForecastDTO update(FinancialForecastDTO financialForecastDTO) {
        log.debug("Request to update FinancialForecast : {}", financialForecastDTO);
        FinancialForecast financialForecast = financialForecastMapper.toEntity(financialForecastDTO);
        financialForecast = financialForecastRepository.save(financialForecast);
        FinancialForecastDTO result = financialForecastMapper.toDto(financialForecast);
        financialForecastSearchRepository.index(financialForecast);
        return result;
    }

    /**
     * Partially update a financialForecast.
     *
     * @param financialForecastDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FinancialForecastDTO> partialUpdate(FinancialForecastDTO financialForecastDTO) {
        log.debug("Request to partially update FinancialForecast : {}", financialForecastDTO);

        return financialForecastRepository
            .findById(financialForecastDTO.getId())
            .map(existingFinancialForecast -> {
                financialForecastMapper.partialUpdate(existingFinancialForecast, financialForecastDTO);

                return existingFinancialForecast;
            })
            .map(financialForecastRepository::save)
            .map(savedFinancialForecast -> {
                financialForecastSearchRepository.save(savedFinancialForecast);

                return savedFinancialForecast;
            })
            .map(financialForecastMapper::toDto);
    }

    /**
     * Get all the financialForecasts.
     *
     * @return the list of entities.
     */
    public List<FinancialForecastDTO> findAll() {
        log.debug("Request to get all FinancialForecasts");
        return financialForecastRepository
            .findAll()
            .stream()
            .map(financialForecastMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one financialForecast by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<FinancialForecastDTO> findOne(String id) {
        log.debug("Request to get FinancialForecast : {}", id);
        return financialForecastRepository.findById(id).map(financialForecastMapper::toDto);
    }

    /**
     * Delete the financialForecast by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete FinancialForecast : {}", id);
        financialForecastRepository.deleteById(id);
        financialForecastSearchRepository.deleteById(id);
    }

    /**
     * Search for the financialForecast corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    public List<FinancialForecastDTO> search(String query) {
        log.debug("Request to search FinancialForecasts for query {}", query);
        return StreamSupport
            .stream(financialForecastSearchRepository.search(query).spliterator(), false)
            .map(financialForecastMapper::toDto)
            .collect(Collectors.toList());
    }
}
