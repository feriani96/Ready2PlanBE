package com.readytoplanbe.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.readytoplanbe.app.IntegrationTest;
import com.readytoplanbe.app.domain.ExpenseForecast;
import com.readytoplanbe.app.repository.ExpenseForecastRepository;
import com.readytoplanbe.app.repository.search.ExpenseForecastSearchRepository;
import com.readytoplanbe.app.service.ExpenseForecastService;
import com.readytoplanbe.app.service.dto.ExpenseForecastDTO;
import com.readytoplanbe.app.service.mapper.ExpenseForecastMapper;
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
 * Integration tests for the {@link ExpenseForecastResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExpenseForecastResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Double DEFAULT_MONTHLY_AMOUNT = 1D;
    private static final Double UPDATED_MONTHLY_AMOUNT = 2D;

    private static final String ENTITY_API_URL = "/api/expense-forecasts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/expense-forecasts";

    @Autowired
    private ExpenseForecastRepository expenseForecastRepository;

    @Mock
    private ExpenseForecastRepository expenseForecastRepositoryMock;

    @Autowired
    private ExpenseForecastMapper expenseForecastMapper;

    @Mock
    private ExpenseForecastService expenseForecastServiceMock;

    @Autowired
    private ExpenseForecastSearchRepository expenseForecastSearchRepository;

    @Autowired
    private MockMvc restExpenseForecastMockMvc;

    private ExpenseForecast expenseForecast;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExpenseForecast createEntity() {
        ExpenseForecast expenseForecast = new ExpenseForecast().label(DEFAULT_LABEL).monthlyAmount(DEFAULT_MONTHLY_AMOUNT);
        return expenseForecast;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExpenseForecast createUpdatedEntity() {
        ExpenseForecast expenseForecast = new ExpenseForecast().label(UPDATED_LABEL).monthlyAmount(UPDATED_MONTHLY_AMOUNT);
        return expenseForecast;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        expenseForecastSearchRepository.deleteAll();
        assertThat(expenseForecastSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        expenseForecastRepository.deleteAll();
        expenseForecast = createEntity();
    }

    @Test
    void createExpenseForecast() throws Exception {
        int databaseSizeBeforeCreate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        // Create the ExpenseForecast
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);
        restExpenseForecastMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ExpenseForecast testExpenseForecast = expenseForecastList.get(expenseForecastList.size() - 1);
        assertThat(testExpenseForecast.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testExpenseForecast.getMonthlyAmount()).isEqualTo(DEFAULT_MONTHLY_AMOUNT);
    }

    @Test
    void createExpenseForecastWithExistingId() throws Exception {
        // Create the ExpenseForecast with an existing ID
        expenseForecast.setId("existing_id");
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);

        int databaseSizeBeforeCreate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restExpenseForecastMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllExpenseForecasts() throws Exception {
        // Initialize the database
        expenseForecastRepository.save(expenseForecast);

        // Get all the expenseForecastList
        restExpenseForecastMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expenseForecast.getId())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].monthlyAmount").value(hasItem(DEFAULT_MONTHLY_AMOUNT.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExpenseForecastsWithEagerRelationshipsIsEnabled() throws Exception {
        when(expenseForecastServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExpenseForecastMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(expenseForecastServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExpenseForecastsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(expenseForecastServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExpenseForecastMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(expenseForecastRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getExpenseForecast() throws Exception {
        // Initialize the database
        expenseForecastRepository.save(expenseForecast);

        // Get the expenseForecast
        restExpenseForecastMockMvc
            .perform(get(ENTITY_API_URL_ID, expenseForecast.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(expenseForecast.getId()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.monthlyAmount").value(DEFAULT_MONTHLY_AMOUNT.doubleValue()));
    }

    @Test
    void getNonExistingExpenseForecast() throws Exception {
        // Get the expenseForecast
        restExpenseForecastMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingExpenseForecast() throws Exception {
        // Initialize the database
        expenseForecastRepository.save(expenseForecast);

        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();
        expenseForecastSearchRepository.save(expenseForecast);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());

        // Update the expenseForecast
        ExpenseForecast updatedExpenseForecast = expenseForecastRepository.findById(expenseForecast.getId()).get();
        updatedExpenseForecast.label(UPDATED_LABEL).monthlyAmount(UPDATED_MONTHLY_AMOUNT);
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(updatedExpenseForecast);

        restExpenseForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, expenseForecastDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        ExpenseForecast testExpenseForecast = expenseForecastList.get(expenseForecastList.size() - 1);
        assertThat(testExpenseForecast.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testExpenseForecast.getMonthlyAmount()).isEqualTo(UPDATED_MONTHLY_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ExpenseForecast> expenseForecastSearchList = IterableUtils.toList(expenseForecastSearchRepository.findAll());
                ExpenseForecast testExpenseForecastSearch = expenseForecastSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testExpenseForecastSearch.getLabel()).isEqualTo(UPDATED_LABEL);
                assertThat(testExpenseForecastSearch.getMonthlyAmount()).isEqualTo(UPDATED_MONTHLY_AMOUNT);
            });
    }

    @Test
    void putNonExistingExpenseForecast() throws Exception {
        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        expenseForecast.setId(UUID.randomUUID().toString());

        // Create the ExpenseForecast
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpenseForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, expenseForecastDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchExpenseForecast() throws Exception {
        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        expenseForecast.setId(UUID.randomUUID().toString());

        // Create the ExpenseForecast
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamExpenseForecast() throws Exception {
        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        expenseForecast.setId(UUID.randomUUID().toString());

        // Create the ExpenseForecast
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseForecastMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateExpenseForecastWithPatch() throws Exception {
        // Initialize the database
        expenseForecastRepository.save(expenseForecast);

        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();

        // Update the expenseForecast using partial update
        ExpenseForecast partialUpdatedExpenseForecast = new ExpenseForecast();
        partialUpdatedExpenseForecast.setId(expenseForecast.getId());

        partialUpdatedExpenseForecast.label(UPDATED_LABEL);

        restExpenseForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpenseForecast.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExpenseForecast))
            )
            .andExpect(status().isOk());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        ExpenseForecast testExpenseForecast = expenseForecastList.get(expenseForecastList.size() - 1);
        assertThat(testExpenseForecast.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testExpenseForecast.getMonthlyAmount()).isEqualTo(DEFAULT_MONTHLY_AMOUNT);
    }

    @Test
    void fullUpdateExpenseForecastWithPatch() throws Exception {
        // Initialize the database
        expenseForecastRepository.save(expenseForecast);

        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();

        // Update the expenseForecast using partial update
        ExpenseForecast partialUpdatedExpenseForecast = new ExpenseForecast();
        partialUpdatedExpenseForecast.setId(expenseForecast.getId());

        partialUpdatedExpenseForecast.label(UPDATED_LABEL).monthlyAmount(UPDATED_MONTHLY_AMOUNT);

        restExpenseForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpenseForecast.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExpenseForecast))
            )
            .andExpect(status().isOk());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        ExpenseForecast testExpenseForecast = expenseForecastList.get(expenseForecastList.size() - 1);
        assertThat(testExpenseForecast.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testExpenseForecast.getMonthlyAmount()).isEqualTo(UPDATED_MONTHLY_AMOUNT);
    }

    @Test
    void patchNonExistingExpenseForecast() throws Exception {
        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        expenseForecast.setId(UUID.randomUUID().toString());

        // Create the ExpenseForecast
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpenseForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, expenseForecastDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchExpenseForecast() throws Exception {
        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        expenseForecast.setId(UUID.randomUUID().toString());

        // Create the ExpenseForecast
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamExpenseForecast() throws Exception {
        int databaseSizeBeforeUpdate = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        expenseForecast.setId(UUID.randomUUID().toString());

        // Create the ExpenseForecast
        ExpenseForecastDTO expenseForecastDTO = expenseForecastMapper.toDto(expenseForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpenseForecastMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(expenseForecastDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExpenseForecast in the database
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteExpenseForecast() throws Exception {
        // Initialize the database
        expenseForecastRepository.save(expenseForecast);
        expenseForecastRepository.save(expenseForecast);
        expenseForecastSearchRepository.save(expenseForecast);

        int databaseSizeBeforeDelete = expenseForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the expenseForecast
        restExpenseForecastMockMvc
            .perform(delete(ENTITY_API_URL_ID, expenseForecast.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ExpenseForecast> expenseForecastList = expenseForecastRepository.findAll();
        assertThat(expenseForecastList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(expenseForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchExpenseForecast() throws Exception {
        // Initialize the database
        expenseForecast = expenseForecastRepository.save(expenseForecast);
        expenseForecastSearchRepository.save(expenseForecast);

        // Search the expenseForecast
        restExpenseForecastMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + expenseForecast.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expenseForecast.getId())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].monthlyAmount").value(hasItem(DEFAULT_MONTHLY_AMOUNT.doubleValue())));
    }
}
