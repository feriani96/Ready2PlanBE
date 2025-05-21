package com.readytoplanbe.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.readytoplanbe.app.IntegrationTest;
import com.readytoplanbe.app.domain.BusinessPlan;
import com.readytoplanbe.app.repository.BusinessPlanRepository;
import com.readytoplanbe.app.repository.search.BusinessPlanSearchRepository;
import com.readytoplanbe.app.service.dto.BusinessPlanDTO;
import com.readytoplanbe.app.service.mapper.BusinessPlanMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link BusinessPlanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BusinessPlanResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_CREATION_DATE = 1L;
    private static final Long UPDATED_CREATION_DATE = 2L;

    private static final String DEFAULT_ENTROPRENEUR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENTROPRENEUR_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/business-plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/business-plans";

    @Autowired
    private BusinessPlanRepository businessPlanRepository;

    @Autowired
    private BusinessPlanMapper businessPlanMapper;

    @Autowired
    private BusinessPlanSearchRepository businessPlanSearchRepository;

    @Autowired
    private MockMvc restBusinessPlanMockMvc;

    private BusinessPlan businessPlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessPlan createEntity() {
        BusinessPlan businessPlan = new BusinessPlan()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .creationDate(DEFAULT_CREATION_DATE)
            .entropreneurName(DEFAULT_ENTROPRENEUR_NAME);
        return businessPlan;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessPlan createUpdatedEntity() {
        BusinessPlan businessPlan = new BusinessPlan()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .creationDate(UPDATED_CREATION_DATE)
            .entropreneurName(UPDATED_ENTROPRENEUR_NAME);
        return businessPlan;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        businessPlanSearchRepository.deleteAll();
        assertThat(businessPlanSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        businessPlanRepository.deleteAll();
        businessPlan = createEntity();
    }

    @Test
    void createBusinessPlan() throws Exception {
        int databaseSizeBeforeCreate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        // Create the BusinessPlan
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);
        restBusinessPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BusinessPlan testBusinessPlan = businessPlanList.get(businessPlanList.size() - 1);
        assertThat(testBusinessPlan.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBusinessPlan.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBusinessPlan.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testBusinessPlan.getEntropreneurName()).isEqualTo(DEFAULT_ENTROPRENEUR_NAME);
    }

    @Test
    void createBusinessPlanWithExistingId() throws Exception {
        // Create the BusinessPlan with an existing ID
        businessPlan.setId("existing_id");
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);

        int databaseSizeBeforeCreate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBusinessPlans() throws Exception {
        // Initialize the database
        businessPlanRepository.save(businessPlan);

        // Get all the businessPlanList
        restBusinessPlanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessPlan.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.intValue())))
            .andExpect(jsonPath("$.[*].entropreneurName").value(hasItem(DEFAULT_ENTROPRENEUR_NAME)));
    }

    @Test
    void getBusinessPlan() throws Exception {
        // Initialize the database
        businessPlanRepository.save(businessPlan);

        // Get the businessPlan
        restBusinessPlanMockMvc
            .perform(get(ENTITY_API_URL_ID, businessPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businessPlan.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.intValue()))
            .andExpect(jsonPath("$.entropreneurName").value(DEFAULT_ENTROPRENEUR_NAME));
    }

    @Test
    void getNonExistingBusinessPlan() throws Exception {
        // Get the businessPlan
        restBusinessPlanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingBusinessPlan() throws Exception {
        // Initialize the database
        businessPlanRepository.save(businessPlan);

        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();
        businessPlanSearchRepository.save(businessPlan);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());

        // Update the businessPlan
        BusinessPlan updatedBusinessPlan = businessPlanRepository.findById(businessPlan.getId()).get();
        updatedBusinessPlan
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .creationDate(UPDATED_CREATION_DATE)
            .entropreneurName(UPDATED_ENTROPRENEUR_NAME);
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(updatedBusinessPlan);

        restBusinessPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessPlanDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isOk());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        BusinessPlan testBusinessPlan = businessPlanList.get(businessPlanList.size() - 1);
        assertThat(testBusinessPlan.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinessPlan.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBusinessPlan.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testBusinessPlan.getEntropreneurName()).isEqualTo(UPDATED_ENTROPRENEUR_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BusinessPlan> businessPlanSearchList = IterableUtils.toList(businessPlanSearchRepository.findAll());
                BusinessPlan testBusinessPlanSearch = businessPlanSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBusinessPlanSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testBusinessPlanSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testBusinessPlanSearch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
                assertThat(testBusinessPlanSearch.getEntropreneurName()).isEqualTo(UPDATED_ENTROPRENEUR_NAME);
            });
    }

    @Test
    void putNonExistingBusinessPlan() throws Exception {
        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        businessPlan.setId(UUID.randomUUID().toString());

        // Create the BusinessPlan
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessPlanDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBusinessPlan() throws Exception {
        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        businessPlan.setId(UUID.randomUUID().toString());

        // Create the BusinessPlan
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBusinessPlan() throws Exception {
        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        businessPlan.setId(UUID.randomUUID().toString());

        // Create the BusinessPlan
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessPlanMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBusinessPlanWithPatch() throws Exception {
        // Initialize the database
        businessPlanRepository.save(businessPlan);

        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();

        // Update the businessPlan using partial update
        BusinessPlan partialUpdatedBusinessPlan = new BusinessPlan();
        partialUpdatedBusinessPlan.setId(businessPlan.getId());

        partialUpdatedBusinessPlan.name(UPDATED_NAME).creationDate(UPDATED_CREATION_DATE);

        restBusinessPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBusinessPlan))
            )
            .andExpect(status().isOk());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        BusinessPlan testBusinessPlan = businessPlanList.get(businessPlanList.size() - 1);
        assertThat(testBusinessPlan.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinessPlan.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBusinessPlan.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testBusinessPlan.getEntropreneurName()).isEqualTo(DEFAULT_ENTROPRENEUR_NAME);
    }

    @Test
    void fullUpdateBusinessPlanWithPatch() throws Exception {
        // Initialize the database
        businessPlanRepository.save(businessPlan);

        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();

        // Update the businessPlan using partial update
        BusinessPlan partialUpdatedBusinessPlan = new BusinessPlan();
        partialUpdatedBusinessPlan.setId(businessPlan.getId());

        partialUpdatedBusinessPlan
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .creationDate(UPDATED_CREATION_DATE)
            .entropreneurName(UPDATED_ENTROPRENEUR_NAME);

        restBusinessPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBusinessPlan))
            )
            .andExpect(status().isOk());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        BusinessPlan testBusinessPlan = businessPlanList.get(businessPlanList.size() - 1);
        assertThat(testBusinessPlan.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinessPlan.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBusinessPlan.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testBusinessPlan.getEntropreneurName()).isEqualTo(UPDATED_ENTROPRENEUR_NAME);
    }

    @Test
    void patchNonExistingBusinessPlan() throws Exception {
        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        businessPlan.setId(UUID.randomUUID().toString());

        // Create the BusinessPlan
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, businessPlanDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBusinessPlan() throws Exception {
        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        businessPlan.setId(UUID.randomUUID().toString());

        // Create the BusinessPlan
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBusinessPlan() throws Exception {
        int databaseSizeBeforeUpdate = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        businessPlan.setId(UUID.randomUUID().toString());

        // Create the BusinessPlan
        BusinessPlanDTO businessPlanDTO = businessPlanMapper.toDto(businessPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessPlanMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(businessPlanDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessPlan in the database
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBusinessPlan() throws Exception {
        // Initialize the database
        businessPlanRepository.save(businessPlan);
        businessPlanRepository.save(businessPlan);
        businessPlanSearchRepository.save(businessPlan);

        int databaseSizeBeforeDelete = businessPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the businessPlan
        restBusinessPlanMockMvc
            .perform(delete(ENTITY_API_URL_ID, businessPlan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BusinessPlan> businessPlanList = businessPlanRepository.findAll();
        assertThat(businessPlanList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(businessPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBusinessPlan() throws Exception {
        // Initialize the database
        businessPlan = businessPlanRepository.save(businessPlan);
        businessPlanSearchRepository.save(businessPlan);

        // Search the businessPlan
        restBusinessPlanMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + businessPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessPlan.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.intValue())))
            .andExpect(jsonPath("$.[*].entropreneurName").value(hasItem(DEFAULT_ENTROPRENEUR_NAME)));
    }
}
