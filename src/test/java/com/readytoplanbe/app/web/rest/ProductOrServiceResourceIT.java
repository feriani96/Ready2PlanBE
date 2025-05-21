package com.readytoplanbe.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.readytoplanbe.app.IntegrationTest;
import com.readytoplanbe.app.domain.ProductOrService;
import com.readytoplanbe.app.repository.ProductOrServiceRepository;
import com.readytoplanbe.app.repository.search.ProductOrServiceSearchRepository;
import com.readytoplanbe.app.service.ProductOrServiceService;
import com.readytoplanbe.app.service.dto.ProductOrServiceDTO;
import com.readytoplanbe.app.service.mapper.ProductOrServiceMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link ProductOrServiceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductOrServiceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_UNIT_PRICE = 1D;
    private static final Double UPDATED_UNIT_PRICE = 2D;

    private static final Integer DEFAULT_ESTIMATED_MONTHLY_SALES = 1;
    private static final Integer UPDATED_ESTIMATED_MONTHLY_SALES = 2;

    private static final Integer DEFAULT_DURATION_IN_MONTHS = 1;
    private static final Integer UPDATED_DURATION_IN_MONTHS = 2;

    private static final String ENTITY_API_URL = "/api/product-or-services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/product-or-services";

    @Autowired
    private ProductOrServiceRepository productOrServiceRepository;

    @Mock
    private ProductOrServiceRepository productOrServiceRepositoryMock;

    @Autowired
    private ProductOrServiceMapper productOrServiceMapper;

    @Mock
    private ProductOrServiceService productOrServiceServiceMock;

    @Autowired
    private ProductOrServiceSearchRepository productOrServiceSearchRepository;

    @Autowired
    private MockMvc restProductOrServiceMockMvc;

    private ProductOrService productOrService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductOrService createEntity() {
        ProductOrService productOrService = new ProductOrService()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .estimatedMonthlySales(DEFAULT_ESTIMATED_MONTHLY_SALES)
            .durationInMonths(DEFAULT_DURATION_IN_MONTHS);
        return productOrService;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductOrService createUpdatedEntity() {
        ProductOrService productOrService = new ProductOrService()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .unitPrice(UPDATED_UNIT_PRICE)
            .estimatedMonthlySales(UPDATED_ESTIMATED_MONTHLY_SALES)
            .durationInMonths(UPDATED_DURATION_IN_MONTHS);
        return productOrService;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        productOrServiceSearchRepository.deleteAll();
        assertThat(productOrServiceSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        productOrServiceRepository.deleteAll();
        productOrService = createEntity();
    }

    @Test
    void createProductOrService() throws Exception {
        int databaseSizeBeforeCreate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        // Create the ProductOrService
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);
        restProductOrServiceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ProductOrService testProductOrService = productOrServiceList.get(productOrServiceList.size() - 1);
        assertThat(testProductOrService.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductOrService.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductOrService.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testProductOrService.getEstimatedMonthlySales()).isEqualTo(DEFAULT_ESTIMATED_MONTHLY_SALES);
        assertThat(testProductOrService.getDurationInMonths()).isEqualTo(DEFAULT_DURATION_IN_MONTHS);
    }

    @Test
    void createProductOrServiceWithExistingId() throws Exception {
        // Create the ProductOrService with an existing ID
        productOrService.setId("existing_id");
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);

        int databaseSizeBeforeCreate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductOrServiceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductOrServices() throws Exception {
        // Initialize the database
        productOrServiceRepository.save(productOrService);

        // Get all the productOrServiceList
        restProductOrServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productOrService.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].estimatedMonthlySales").value(hasItem(DEFAULT_ESTIMATED_MONTHLY_SALES)))
            .andExpect(jsonPath("$.[*].durationInMonths").value(hasItem(DEFAULT_DURATION_IN_MONTHS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductOrServicesWithEagerRelationshipsIsEnabled() throws Exception {
        when(productOrServiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductOrServiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productOrServiceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductOrServicesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productOrServiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductOrServiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productOrServiceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getProductOrService() throws Exception {
        // Initialize the database
        productOrServiceRepository.save(productOrService);

        // Get the productOrService
        restProductOrServiceMockMvc
            .perform(get(ENTITY_API_URL_ID, productOrService.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productOrService.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.estimatedMonthlySales").value(DEFAULT_ESTIMATED_MONTHLY_SALES))
            .andExpect(jsonPath("$.durationInMonths").value(DEFAULT_DURATION_IN_MONTHS));
    }

    @Test
    void getNonExistingProductOrService() throws Exception {
        // Get the productOrService
        restProductOrServiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingProductOrService() throws Exception {
        // Initialize the database
        productOrServiceRepository.save(productOrService);

        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();
        productOrServiceSearchRepository.save(productOrService);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());

        // Update the productOrService
        ProductOrService updatedProductOrService = productOrServiceRepository.findById(productOrService.getId()).get();
        updatedProductOrService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .unitPrice(UPDATED_UNIT_PRICE)
            .estimatedMonthlySales(UPDATED_ESTIMATED_MONTHLY_SALES)
            .durationInMonths(UPDATED_DURATION_IN_MONTHS);
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(updatedProductOrService);

        restProductOrServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productOrServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        ProductOrService testProductOrService = productOrServiceList.get(productOrServiceList.size() - 1);
        assertThat(testProductOrService.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductOrService.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductOrService.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testProductOrService.getEstimatedMonthlySales()).isEqualTo(UPDATED_ESTIMATED_MONTHLY_SALES);
        assertThat(testProductOrService.getDurationInMonths()).isEqualTo(UPDATED_DURATION_IN_MONTHS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductOrService> productOrServiceSearchList = IterableUtils.toList(productOrServiceSearchRepository.findAll());
                ProductOrService testProductOrServiceSearch = productOrServiceSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testProductOrServiceSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testProductOrServiceSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testProductOrServiceSearch.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
                assertThat(testProductOrServiceSearch.getEstimatedMonthlySales()).isEqualTo(UPDATED_ESTIMATED_MONTHLY_SALES);
                assertThat(testProductOrServiceSearch.getDurationInMonths()).isEqualTo(UPDATED_DURATION_IN_MONTHS);
            });
    }

    @Test
    void putNonExistingProductOrService() throws Exception {
        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        productOrService.setId(UUID.randomUUID().toString());

        // Create the ProductOrService
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductOrServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productOrServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductOrService() throws Exception {
        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        productOrService.setId(UUID.randomUUID().toString());

        // Create the ProductOrService
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductOrServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductOrService() throws Exception {
        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        productOrService.setId(UUID.randomUUID().toString());

        // Create the ProductOrService
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductOrServiceMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductOrServiceWithPatch() throws Exception {
        // Initialize the database
        productOrServiceRepository.save(productOrService);

        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();

        // Update the productOrService using partial update
        ProductOrService partialUpdatedProductOrService = new ProductOrService();
        partialUpdatedProductOrService.setId(productOrService.getId());

        partialUpdatedProductOrService.estimatedMonthlySales(UPDATED_ESTIMATED_MONTHLY_SALES).durationInMonths(UPDATED_DURATION_IN_MONTHS);

        restProductOrServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductOrService.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductOrService))
            )
            .andExpect(status().isOk());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        ProductOrService testProductOrService = productOrServiceList.get(productOrServiceList.size() - 1);
        assertThat(testProductOrService.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductOrService.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductOrService.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testProductOrService.getEstimatedMonthlySales()).isEqualTo(UPDATED_ESTIMATED_MONTHLY_SALES);
        assertThat(testProductOrService.getDurationInMonths()).isEqualTo(UPDATED_DURATION_IN_MONTHS);
    }

    @Test
    void fullUpdateProductOrServiceWithPatch() throws Exception {
        // Initialize the database
        productOrServiceRepository.save(productOrService);

        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();

        // Update the productOrService using partial update
        ProductOrService partialUpdatedProductOrService = new ProductOrService();
        partialUpdatedProductOrService.setId(productOrService.getId());

        partialUpdatedProductOrService
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .unitPrice(UPDATED_UNIT_PRICE)
            .estimatedMonthlySales(UPDATED_ESTIMATED_MONTHLY_SALES)
            .durationInMonths(UPDATED_DURATION_IN_MONTHS);

        restProductOrServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductOrService.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductOrService))
            )
            .andExpect(status().isOk());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        ProductOrService testProductOrService = productOrServiceList.get(productOrServiceList.size() - 1);
        assertThat(testProductOrService.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductOrService.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductOrService.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testProductOrService.getEstimatedMonthlySales()).isEqualTo(UPDATED_ESTIMATED_MONTHLY_SALES);
        assertThat(testProductOrService.getDurationInMonths()).isEqualTo(UPDATED_DURATION_IN_MONTHS);
    }

    @Test
    void patchNonExistingProductOrService() throws Exception {
        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        productOrService.setId(UUID.randomUUID().toString());

        // Create the ProductOrService
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductOrServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productOrServiceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductOrService() throws Exception {
        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        productOrService.setId(UUID.randomUUID().toString());

        // Create the ProductOrService
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductOrServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductOrService() throws Exception {
        int databaseSizeBeforeUpdate = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        productOrService.setId(UUID.randomUUID().toString());

        // Create the ProductOrService
        ProductOrServiceDTO productOrServiceDTO = productOrServiceMapper.toDto(productOrService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductOrServiceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productOrServiceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductOrService in the database
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductOrService() throws Exception {
        // Initialize the database
        productOrServiceRepository.save(productOrService);
        productOrServiceRepository.save(productOrService);
        productOrServiceSearchRepository.save(productOrService);

        int databaseSizeBeforeDelete = productOrServiceRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productOrService
        restProductOrServiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, productOrService.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductOrService> productOrServiceList = productOrServiceRepository.findAll();
        assertThat(productOrServiceList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productOrServiceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductOrService() throws Exception {
        // Initialize the database
        productOrService = productOrServiceRepository.save(productOrService);
        productOrServiceSearchRepository.save(productOrService);

        // Search the productOrService
        restProductOrServiceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + productOrService.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productOrService.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].estimatedMonthlySales").value(hasItem(DEFAULT_ESTIMATED_MONTHLY_SALES)))
            .andExpect(jsonPath("$.[*].durationInMonths").value(hasItem(DEFAULT_DURATION_IN_MONTHS)));
    }
}
