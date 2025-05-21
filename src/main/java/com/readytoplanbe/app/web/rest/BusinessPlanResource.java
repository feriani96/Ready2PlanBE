package com.readytoplanbe.app.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.readytoplanbe.app.repository.BusinessPlanRepository;
import com.readytoplanbe.app.service.BusinessPlanService;
import com.readytoplanbe.app.service.dto.BusinessPlanDTO;
import com.readytoplanbe.app.service.dto.FinancialSummaryDTO;
import com.readytoplanbe.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.readytoplanbe.app.domain.BusinessPlan}.
 */
@RestController
@RequestMapping("/api")
public class BusinessPlanResource {

    private final Logger log = LoggerFactory.getLogger(BusinessPlanResource.class);

    private static final String ENTITY_NAME = "businessPlan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessPlanService businessPlanService;

    private final BusinessPlanRepository businessPlanRepository;

    public BusinessPlanResource(BusinessPlanService businessPlanService, BusinessPlanRepository businessPlanRepository) {
        this.businessPlanService = businessPlanService;
        this.businessPlanRepository = businessPlanRepository;
    }

    /**
     * {@code POST  /business-plans} : Create a new businessPlan.
     *
     * @param businessPlanDTO the businessPlanDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessPlanDTO, or with status {@code 400 (Bad Request)} if the businessPlan has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/business-plans")
    public ResponseEntity<BusinessPlanDTO> createBusinessPlan(@RequestBody BusinessPlanDTO businessPlanDTO) throws URISyntaxException {
        log.debug("REST request to save BusinessPlan : {}", businessPlanDTO);
        if (businessPlanDTO.getId() != null) {
            throw new BadRequestAlertException("A new businessPlan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BusinessPlanDTO result = businessPlanService.save(businessPlanDTO);
        return ResponseEntity
            .created(new URI("/api/business-plans/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /business-plans/:id} : Updates an existing businessPlan.
     *
     * @param id the id of the businessPlanDTO to save.
     * @param businessPlanDTO the businessPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessPlanDTO,
     * or with status {@code 400 (Bad Request)} if the businessPlanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessPlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/business-plans/{id}")
    public ResponseEntity<BusinessPlanDTO> updateBusinessPlan(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody BusinessPlanDTO businessPlanDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BusinessPlan : {}, {}", id, businessPlanDTO);
        if (businessPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BusinessPlanDTO result = businessPlanService.update(businessPlanDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessPlanDTO.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /business-plans/:id} : Partial updates given fields of an existing businessPlan, field will ignore if it is null
     *
     * @param id the id of the businessPlanDTO to save.
     * @param businessPlanDTO the businessPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessPlanDTO,
     * or with status {@code 400 (Bad Request)} if the businessPlanDTO is not valid,
     * or with status {@code 404 (Not Found)} if the businessPlanDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the businessPlanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/business-plans/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BusinessPlanDTO> partialUpdateBusinessPlan(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody BusinessPlanDTO businessPlanDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BusinessPlan partially : {}, {}", id, businessPlanDTO);
        if (businessPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!businessPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BusinessPlanDTO> result = businessPlanService.partialUpdate(businessPlanDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessPlanDTO.getId())
        );
    }

    /**
     * {@code GET  /business-plans} : get all the businessPlans.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessPlans in body.
     */
    @GetMapping("/business-plans")
    public List<BusinessPlanDTO> getAllBusinessPlans(@RequestParam(required = false) String filter) {
        if ("forecast-is-null".equals(filter)) {
            log.debug("REST request to get all BusinessPlans where forecast is null");
            return businessPlanService.findAllWhereForecastIsNull();
        }
        log.debug("REST request to get all BusinessPlans");
        return businessPlanService.findAll();
    }

    /**
     * {@code GET  /business-plans/:id} : get the "id" businessPlan.
     *
     * @param id the id of the businessPlanDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessPlanDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/business-plans/{id}")
    public ResponseEntity<BusinessPlanDTO> getBusinessPlan(@PathVariable String id) {
        log.debug("REST request to get BusinessPlan : {}", id);
        Optional<BusinessPlanDTO> businessPlanDTO = businessPlanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessPlanDTO);
    }

    /**
     * {@code DELETE  /business-plans/:id} : delete the "id" businessPlan.
     *
     * @param id the id of the businessPlanDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/business-plans/{id}")
    public ResponseEntity<Void> deleteBusinessPlan(@PathVariable String id) {
        log.debug("REST request to delete BusinessPlan : {}", id);
        businessPlanService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/business-plans?query=:query} : search for the businessPlan corresponding
     * to the query.
     *
     * @param query the query of the businessPlan search.
     * @return the result of the search.
     */
    @GetMapping("/_search/business-plans")
    public List<BusinessPlanDTO> searchBusinessPlans(@RequestParam String query) {
        log.debug("REST request to search BusinessPlans for query {}", query);
        return businessPlanService.search(query);
    }
    @GetMapping("/business-plans/{id}/summary")
    public ResponseEntity<FinancialSummaryDTO> getSummary(@PathVariable String id) {
        FinancialSummaryDTO summary = businessPlanService.getFinancialSummary(id);
        return ResponseEntity.ok(summary);
    }

}
