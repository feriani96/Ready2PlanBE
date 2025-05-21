package com.readytoplanbe.app.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.readytoplanbe.app.domain.RevenueForecast;
import com.readytoplanbe.app.repository.RevenueForecastRepository;
import com.readytoplanbe.app.repository.search.RevenueForecastSearchRepository;
import com.readytoplanbe.app.service.dto.RevenueForecastDTO;
import com.readytoplanbe.app.service.mapper.RevenueForecastMapper;
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
 * Service Implementation for managing {@link RevenueForecast}.
 */
@Service
public class RevenueForecastService {

    private final Logger log = LoggerFactory.getLogger(RevenueForecastService.class);

    private final RevenueForecastRepository revenueForecastRepository;

    private final RevenueForecastMapper revenueForecastMapper;

    private final RevenueForecastSearchRepository revenueForecastSearchRepository;

    public RevenueForecastService(
        RevenueForecastRepository revenueForecastRepository,
        RevenueForecastMapper revenueForecastMapper,
        RevenueForecastSearchRepository revenueForecastSearchRepository
    ) {
        this.revenueForecastRepository = revenueForecastRepository;
        this.revenueForecastMapper = revenueForecastMapper;
        this.revenueForecastSearchRepository = revenueForecastSearchRepository;
    }

    /**
     * Save a revenueForecast.
     *
     * @param revenueForecastDTO the entity to save.
     * @return the persisted entity.
     */
    public RevenueForecastDTO save(RevenueForecastDTO revenueForecastDTO) {
        log.debug("Request to save RevenueForecast : {}", revenueForecastDTO);
        RevenueForecast revenueForecast = revenueForecastMapper.toEntity(revenueForecastDTO);
        revenueForecast = revenueForecastRepository.save(revenueForecast);
        RevenueForecastDTO result = revenueForecastMapper.toDto(revenueForecast);
        revenueForecastSearchRepository.index(revenueForecast);
        return result;
    }

    /**
     * Update a revenueForecast.
     *
     * @param revenueForecastDTO the entity to save.
     * @return the persisted entity.
     */
    public RevenueForecastDTO update(RevenueForecastDTO revenueForecastDTO) {
        log.debug("Request to update RevenueForecast : {}", revenueForecastDTO);
        RevenueForecast revenueForecast = revenueForecastMapper.toEntity(revenueForecastDTO);
        revenueForecast = revenueForecastRepository.save(revenueForecast);
        RevenueForecastDTO result = revenueForecastMapper.toDto(revenueForecast);
        revenueForecastSearchRepository.index(revenueForecast);
        return result;
    }

    /**
     * Partially update a revenueForecast.
     *
     * @param revenueForecastDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RevenueForecastDTO> partialUpdate(RevenueForecastDTO revenueForecastDTO) {
        log.debug("Request to partially update RevenueForecast : {}", revenueForecastDTO);

        return revenueForecastRepository
            .findById(revenueForecastDTO.getId())
            .map(existingRevenueForecast -> {
                revenueForecastMapper.partialUpdate(existingRevenueForecast, revenueForecastDTO);

                return existingRevenueForecast;
            })
            .map(revenueForecastRepository::save)
            .map(savedRevenueForecast -> {
                revenueForecastSearchRepository.save(savedRevenueForecast);

                return savedRevenueForecast;
            })
            .map(revenueForecastMapper::toDto);
    }

    /**
     * Get all the revenueForecasts.
     *
     * @return the list of entities.
     */
    public List<RevenueForecastDTO> findAll() {
        log.debug("Request to get all RevenueForecasts");
        return revenueForecastRepository
            .findAll()
            .stream()
            .map(revenueForecastMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the revenueForecasts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RevenueForecastDTO> findAllWithEagerRelationships(Pageable pageable) {
        return revenueForecastRepository.findAllWithEagerRelationships(pageable).map(revenueForecastMapper::toDto);
    }

    /**
     * Get one revenueForecast by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<RevenueForecastDTO> findOne(String id) {
        log.debug("Request to get RevenueForecast : {}", id);
        return revenueForecastRepository.findOneWithEagerRelationships(id).map(revenueForecastMapper::toDto);
    }

    /**
     * Delete the revenueForecast by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete RevenueForecast : {}", id);
        revenueForecastRepository.deleteById(id);
        revenueForecastSearchRepository.deleteById(id);
    }

    /**
     * Search for the revenueForecast corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    public List<RevenueForecastDTO> search(String query) {
        log.debug("Request to search RevenueForecasts for query {}", query);
        return StreamSupport
            .stream(revenueForecastSearchRepository.search(query).spliterator(), false)
            .map(revenueForecastMapper::toDto)
            .collect(Collectors.toList());
    }
}
