package com.readytoplanbe.app.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.readytoplanbe.app.repository.ProductOrServiceRepository;
import com.readytoplanbe.app.service.ProductOrServiceService;
import com.readytoplanbe.app.service.dto.ProductOrServiceDTO;
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
 * REST controller for managing {@link com.readytoplanbe.app.domain.ProductOrService}.
 */
@RestController
@RequestMapping("/api")
public class ProductOrServiceResource {

    private final Logger log = LoggerFactory.getLogger(ProductOrServiceResource.class);

    private static final String ENTITY_NAME = "productOrService";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductOrServiceService productOrServiceService;

    private final ProductOrServiceRepository productOrServiceRepository;

    public ProductOrServiceResource(
        ProductOrServiceService productOrServiceService,
        ProductOrServiceRepository productOrServiceRepository
    ) {
        this.productOrServiceService = productOrServiceService;
        this.productOrServiceRepository = productOrServiceRepository;
    }

    /**
     * {@code POST  /product-or-services} : Create a new productOrService.
     *
     * @param productOrServiceDTO the productOrServiceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productOrServiceDTO, or with status {@code 400 (Bad Request)} if the productOrService has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-or-services")
    public ResponseEntity<ProductOrServiceDTO> createProductOrService(@RequestBody ProductOrServiceDTO productOrServiceDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductOrService : {}", productOrServiceDTO);
        if (productOrServiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new productOrService cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductOrServiceDTO result = productOrServiceService.save(productOrServiceDTO);
        return ResponseEntity
            .created(new URI("/api/product-or-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /product-or-services/:id} : Updates an existing productOrService.
     *
     * @param id the id of the productOrServiceDTO to save.
     * @param productOrServiceDTO the productOrServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productOrServiceDTO,
     * or with status {@code 400 (Bad Request)} if the productOrServiceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productOrServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-or-services/{id}")
    public ResponseEntity<ProductOrServiceDTO> updateProductOrService(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody ProductOrServiceDTO productOrServiceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductOrService : {}, {}", id, productOrServiceDTO);
        if (productOrServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productOrServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productOrServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductOrServiceDTO result = productOrServiceService.update(productOrServiceDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productOrServiceDTO.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /product-or-services/:id} : Partial updates given fields of an existing productOrService, field will ignore if it is null
     *
     * @param id the id of the productOrServiceDTO to save.
     * @param productOrServiceDTO the productOrServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productOrServiceDTO,
     * or with status {@code 400 (Bad Request)} if the productOrServiceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productOrServiceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productOrServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-or-services/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductOrServiceDTO> partialUpdateProductOrService(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody ProductOrServiceDTO productOrServiceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductOrService partially : {}, {}", id, productOrServiceDTO);
        if (productOrServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productOrServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productOrServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductOrServiceDTO> result = productOrServiceService.partialUpdate(productOrServiceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productOrServiceDTO.getId())
        );
    }

    /**
     * {@code GET  /product-or-services} : get all the productOrServices.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productOrServices in body.
     */
    @GetMapping("/product-or-services")
    public List<ProductOrServiceDTO> getAllProductOrServices(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all ProductOrServices");
        return productOrServiceService.findAll();
    }

    /**
     * {@code GET  /product-or-services/:id} : get the "id" productOrService.
     *
     * @param id the id of the productOrServiceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productOrServiceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-or-services/{id}")
    public ResponseEntity<ProductOrServiceDTO> getProductOrService(@PathVariable String id) {
        log.debug("REST request to get ProductOrService : {}", id);
        Optional<ProductOrServiceDTO> productOrServiceDTO = productOrServiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productOrServiceDTO);
    }

    /**
     * {@code DELETE  /product-or-services/:id} : delete the "id" productOrService.
     *
     * @param id the id of the productOrServiceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-or-services/{id}")
    public ResponseEntity<Void> deleteProductOrService(@PathVariable String id) {
        log.debug("REST request to delete ProductOrService : {}", id);
        productOrServiceService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/product-or-services?query=:query} : search for the productOrService corresponding
     * to the query.
     *
     * @param query the query of the productOrService search.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-or-services")
    public List<ProductOrServiceDTO> searchProductOrServices(@RequestParam String query) {
        log.debug("REST request to search ProductOrServices for query {}", query);
        return productOrServiceService.search(query);
    }
}
