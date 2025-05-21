package com.readytoplanbe.app.service;

import com.readytoplanbe.app.domain.BusinessPlan;
import com.readytoplanbe.app.domain.ProductOrService;
import com.readytoplanbe.app.repository.BusinessPlanRepository;
import com.readytoplanbe.app.repository.ProductOrServiceRepository;
import com.readytoplanbe.app.repository.search.ProductOrServiceSearchRepository;
import com.readytoplanbe.app.service.dto.ProductOrServiceDTO;
import com.readytoplanbe.app.service.mapper.ProductOrServiceMapper;
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
 * Service Implementation for managing {@link ProductOrService}.
 */
@Service
public class ProductOrServiceService {

    private final Logger log = LoggerFactory.getLogger(ProductOrServiceService.class);

    private final ProductOrServiceRepository productOrServiceRepository;

    private final ProductOrServiceMapper productOrServiceMapper;

    private final ProductOrServiceSearchRepository productOrServiceSearchRepository;
    private final BusinessPlanRepository businessPlanRepository;

    public ProductOrServiceService(
        ProductOrServiceRepository productOrServiceRepository,
        ProductOrServiceMapper productOrServiceMapper,
        ProductOrServiceSearchRepository productOrServiceSearchRepository,
        BusinessPlanRepository businessPlanRepository) {
        this.productOrServiceRepository = productOrServiceRepository;
        this.productOrServiceMapper = productOrServiceMapper;
        this.productOrServiceSearchRepository = productOrServiceSearchRepository;
        this.businessPlanRepository = businessPlanRepository;
    }

    /**
     * Save a productOrService.
     *
     * @param productOrServiceDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductOrServiceDTO save(ProductOrServiceDTO productOrServiceDTO) {
        log.debug("Request to save ProductOrService : {}", productOrServiceDTO);

        ProductOrService productOrService = productOrServiceMapper.toEntity(productOrServiceDTO);

        // Vérifier que le BusinessPlan est bien présent
        if (productOrService.getBusinessPlan() != null && productOrService.getBusinessPlan().getId() != null) {
            BusinessPlan fullBusinessPlan = businessPlanRepository
                .findById(productOrService.getBusinessPlan().getId())
                .orElseThrow(() -> new RuntimeException("BusinessPlan not found"));

            productOrService.setBusinessPlan(fullBusinessPlan);
        }

        productOrService = productOrServiceRepository.save(productOrService);
        ProductOrServiceDTO result = productOrServiceMapper.toDto(productOrService);
        productOrServiceSearchRepository.index(productOrService);

        return result;
    }


    /**
     * Update a productOrService.
     *
     * @param productOrServiceDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductOrServiceDTO update(ProductOrServiceDTO productOrServiceDTO) {
        log.debug("Request to update ProductOrService : {}", productOrServiceDTO);
        ProductOrService productOrService = productOrServiceMapper.toEntity(productOrServiceDTO);
        productOrService = productOrServiceRepository.save(productOrService);
        ProductOrServiceDTO result = productOrServiceMapper.toDto(productOrService);
        productOrServiceSearchRepository.index(productOrService);
        return result;
    }

    /**
     * Partially update a productOrService.
     *
     * @param productOrServiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductOrServiceDTO> partialUpdate(ProductOrServiceDTO productOrServiceDTO) {
        log.debug("Request to partially update ProductOrService : {}", productOrServiceDTO);

        return productOrServiceRepository
            .findById(productOrServiceDTO.getId())
            .map(existingProductOrService -> {
                productOrServiceMapper.partialUpdate(existingProductOrService, productOrServiceDTO);

                return existingProductOrService;
            })
            .map(productOrServiceRepository::save)
            .map(savedProductOrService -> {
                productOrServiceSearchRepository.save(savedProductOrService);

                return savedProductOrService;
            })
            .map(productOrServiceMapper::toDto);
    }

    /**
     * Get all the productOrServices.
     *
     * @return the list of entities.
     */
    public List<ProductOrServiceDTO> findAll() {
        log.debug("Request to get all ProductOrServices");
        return productOrServiceRepository
            .findAll()
            .stream()
            .map(productOrServiceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the productOrServices with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductOrServiceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productOrServiceRepository.findAllWithEagerRelationships(pageable).map(productOrServiceMapper::toDto);
    }

    /**
     * Get one productOrService by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ProductOrServiceDTO> findOne(String id) {
        log.debug("Request to get ProductOrService : {}", id);
        return productOrServiceRepository.findOneWithEagerRelationships(id).map(productOrServiceMapper::toDto);
    }

    /**
     * Delete the productOrService by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete ProductOrService : {}", id);
        productOrServiceRepository.deleteById(id);
        productOrServiceSearchRepository.deleteById(id);
    }

    /**
     * Search for the productOrService corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    public List<ProductOrServiceDTO> search(String query) {
        log.debug("Request to search ProductOrServices for query {}", query);
        return StreamSupport
            .stream(productOrServiceSearchRepository.search(query).spliterator(), false)
            .map(productOrServiceMapper::toDto)
            .collect(Collectors.toList());
    }
}
