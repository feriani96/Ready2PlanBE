package com.readytoplanbe.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.readytoplanbe.app.IntegrationTest;
import com.readytoplanbe.app.domain.FinancialForecast;
import com.readytoplanbe.app.repository.FinancialForecastRepository;
import com.readytoplanbe.app.repository.search.FinancialForecastSearchRepository;
import com.readytoplanbe.app.service.dto.FinancialForecastDTO;
import com.readytoplanbe.app.service.mapper.FinancialForecastMapper;
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
 * Integration tests for the {@link FinancialForecastResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FinancialForecastResourceIT {

    private static final Long DEFAULT_START_DATE = 1L;
    private static final Long UPDATED_START_DATE = 2L;

    private static final Integer DEFAULT_DURATION_IN_MONTHS = 1;
    private static final Integer UPDATED_DURATION_IN_MONTHS = 2;

    private static final String ENTITY_API_URL = "/api/financial-forecasts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/financial-forecasts";

    @Autowired
    private FinancialForecastRepository financialForecastRepository;

    @Autowired
    private FinancialForecastMapper financialForecastMapper;

    @Autowired
    private FinancialForecastSearchRepository financialForecastSearchRepository;

    @Autowired
    private MockMvc restFinancialForecastMockMvc;

    private FinancialForecast financialForecast;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinancialForecast createEntity() {
        FinancialForecast financialForecast = new FinancialForecast()
            .startDate(DEFAULT_START_DATE)
            .durationInMonths(DEFAULT_DURATION_IN_MONTHS);
        return financialForecast;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinancialForecast createUpdatedEntity() {
        FinancialForecast financialForecast = new FinancialForecast()
            .startDate(UPDATED_START_DATE)
            .durationInMonths(UPDATED_DURATION_IN_MONTHS);
        return financialForecast;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        financialForecastSearchRepository.deleteAll();
        assertThat(financialForecastSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        financialForecastRepository.deleteAll();
        financialForecast = createEntity();
    }

    @Test
    void createFinancialForecast() throws Exception {
        int databaseSizeBeforeCreate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        // Create the FinancialForecast
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);
        restFinancialForecastMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        FinancialForecast testFinancialForecast = financialForecastList.get(financialForecastList.size() - 1);
        assertThat(testFinancialForecast.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testFinancialForecast.getDurationInMonths()).isEqualTo(DEFAULT_DURATION_IN_MONTHS);
    }

    @Test
    void createFinancialForecastWithExistingId() throws Exception {
        // Create the FinancialForecast with an existing ID
        financialForecast.setId("existing_id");
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);

        int databaseSizeBeforeCreate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFinancialForecastMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllFinancialForecasts() throws Exception {
        // Initialize the database
        financialForecastRepository.save(financialForecast);

        // Get all the financialForecastList
        restFinancialForecastMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financialForecast.getId())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.intValue())))
            .andExpect(jsonPath("$.[*].durationInMonths").value(hasItem(DEFAULT_DURATION_IN_MONTHS)));
    }

    @Test
    void getFinancialForecast() throws Exception {
        // Initialize the database
        financialForecastRepository.save(financialForecast);

        // Get the financialForecast
        restFinancialForecastMockMvc
            .perform(get(ENTITY_API_URL_ID, financialForecast.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(financialForecast.getId()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.intValue()))
            .andExpect(jsonPath("$.durationInMonths").value(DEFAULT_DURATION_IN_MONTHS));
    }

    @Test
    void getNonExistingFinancialForecast() throws Exception {
        // Get the financialForecast
        restFinancialForecastMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingFinancialForecast() throws Exception {
        // Initialize the database
        financialForecastRepository.save(financialForecast);

        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();
        financialForecastSearchRepository.save(financialForecast);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());

        // Update the financialForecast
        FinancialForecast updatedFinancialForecast = financialForecastRepository.findById(financialForecast.getId()).get();
        updatedFinancialForecast.startDate(UPDATED_START_DATE).durationInMonths(UPDATED_DURATION_IN_MONTHS);
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(updatedFinancialForecast);

        restFinancialForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financialForecastDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isOk());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        FinancialForecast testFinancialForecast = financialForecastList.get(financialForecastList.size() - 1);
        assertThat(testFinancialForecast.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testFinancialForecast.getDurationInMonths()).isEqualTo(UPDATED_DURATION_IN_MONTHS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<FinancialForecast> financialForecastSearchList = IterableUtils.toList(financialForecastSearchRepository.findAll());
                FinancialForecast testFinancialForecastSearch = financialForecastSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFinancialForecastSearch.getStartDate()).isEqualTo(UPDATED_START_DATE);
                assertThat(testFinancialForecastSearch.getDurationInMonths()).isEqualTo(UPDATED_DURATION_IN_MONTHS);
            });
    }

    @Test
    void putNonExistingFinancialForecast() throws Exception {
        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        financialForecast.setId(UUID.randomUUID().toString());

        // Create the FinancialForecast
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinancialForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financialForecastDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchFinancialForecast() throws Exception {
        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        financialForecast.setId(UUID.randomUUID().toString());

        // Create the FinancialForecast
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamFinancialForecast() throws Exception {
        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        financialForecast.setId(UUID.randomUUID().toString());

        // Create the FinancialForecast
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialForecastMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateFinancialForecastWithPatch() throws Exception {
        // Initialize the database
        financialForecastRepository.save(financialForecast);

        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();

        // Update the financialForecast using partial update
        FinancialForecast partialUpdatedFinancialForecast = new FinancialForecast();
        partialUpdatedFinancialForecast.setId(financialForecast.getId());

        restFinancialForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinancialForecast.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinancialForecast))
            )
            .andExpect(status().isOk());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        FinancialForecast testFinancialForecast = financialForecastList.get(financialForecastList.size() - 1);
        assertThat(testFinancialForecast.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testFinancialForecast.getDurationInMonths()).isEqualTo(DEFAULT_DURATION_IN_MONTHS);
    }

    @Test
    void fullUpdateFinancialForecastWithPatch() throws Exception {
        // Initialize the database
        financialForecastRepository.save(financialForecast);

        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();

        // Update the financialForecast using partial update
        FinancialForecast partialUpdatedFinancialForecast = new FinancialForecast();
        partialUpdatedFinancialForecast.setId(financialForecast.getId());

        partialUpdatedFinancialForecast.startDate(UPDATED_START_DATE).durationInMonths(UPDATED_DURATION_IN_MONTHS);

        restFinancialForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinancialForecast.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinancialForecast))
            )
            .andExpect(status().isOk());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        FinancialForecast testFinancialForecast = financialForecastList.get(financialForecastList.size() - 1);
        assertThat(testFinancialForecast.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testFinancialForecast.getDurationInMonths()).isEqualTo(UPDATED_DURATION_IN_MONTHS);
    }

    @Test
    void patchNonExistingFinancialForecast() throws Exception {
        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        financialForecast.setId(UUID.randomUUID().toString());

        // Create the FinancialForecast
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinancialForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, financialForecastDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchFinancialForecast() throws Exception {
        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        financialForecast.setId(UUID.randomUUID().toString());

        // Create the FinancialForecast
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamFinancialForecast() throws Exception {
        int databaseSizeBeforeUpdate = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        financialForecast.setId(UUID.randomUUID().toString());

        // Create the FinancialForecast
        FinancialForecastDTO financialForecastDTO = financialForecastMapper.toDto(financialForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinancialForecastMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financialForecastDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinancialForecast in the database
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteFinancialForecast() throws Exception {
        // Initialize the database
        financialForecastRepository.save(financialForecast);
        financialForecastRepository.save(financialForecast);
        financialForecastSearchRepository.save(financialForecast);

        int databaseSizeBeforeDelete = financialForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the financialForecast
        restFinancialForecastMockMvc
            .perform(delete(ENTITY_API_URL_ID, financialForecast.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FinancialForecast> financialForecastList = financialForecastRepository.findAll();
        assertThat(financialForecastList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financialForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchFinancialForecast() throws Exception {
        // Initialize the database
        financialForecast = financialForecastRepository.save(financialForecast);
        financialForecastSearchRepository.save(financialForecast);

        // Search the financialForecast
        restFinancialForecastMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + financialForecast.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financialForecast.getId())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.intValue())))
            .andExpect(jsonPath("$.[*].durationInMonths").value(hasItem(DEFAULT_DURATION_IN_MONTHS)));
    }
}
