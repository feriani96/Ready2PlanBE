package com.readytoplanbe.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.readytoplanbe.app.IntegrationTest;
import com.readytoplanbe.app.domain.RevenueForecast;
import com.readytoplanbe.app.repository.RevenueForecastRepository;
import com.readytoplanbe.app.repository.search.RevenueForecastSearchRepository;
import com.readytoplanbe.app.service.RevenueForecastService;
import com.readytoplanbe.app.service.dto.RevenueForecastDTO;
import com.readytoplanbe.app.service.mapper.RevenueForecastMapper;
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
 * Integration tests for the {@link RevenueForecastResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RevenueForecastResourceIT {

    private static final Integer DEFAULT_MONTH = 1;
    private static final Integer UPDATED_MONTH = 2;

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final Double DEFAULT_UNITS_SOLD = 1D;
    private static final Double UPDATED_UNITS_SOLD = 2D;

    private static final Double DEFAULT_TOTAL_REVENUE = 1D;
    private static final Double UPDATED_TOTAL_REVENUE = 2D;

    private static final String ENTITY_API_URL = "/api/revenue-forecasts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/revenue-forecasts";

    @Autowired
    private RevenueForecastRepository revenueForecastRepository;

    @Mock
    private RevenueForecastRepository revenueForecastRepositoryMock;

    @Autowired
    private RevenueForecastMapper revenueForecastMapper;

    @Mock
    private RevenueForecastService revenueForecastServiceMock;

    @Autowired
    private RevenueForecastSearchRepository revenueForecastSearchRepository;

    @Autowired
    private MockMvc restRevenueForecastMockMvc;

    private RevenueForecast revenueForecast;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RevenueForecast createEntity() {
        RevenueForecast revenueForecast = new RevenueForecast()
            .month(DEFAULT_MONTH)
            .year(DEFAULT_YEAR)
            .unitsSold(DEFAULT_UNITS_SOLD)
            .totalRevenue(DEFAULT_TOTAL_REVENUE);
        return revenueForecast;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RevenueForecast createUpdatedEntity() {
        RevenueForecast revenueForecast = new RevenueForecast()
            .month(UPDATED_MONTH)
            .year(UPDATED_YEAR)
            .unitsSold(UPDATED_UNITS_SOLD)
            .totalRevenue(UPDATED_TOTAL_REVENUE);
        return revenueForecast;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        revenueForecastSearchRepository.deleteAll();
        assertThat(revenueForecastSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        revenueForecastRepository.deleteAll();
        revenueForecast = createEntity();
    }

    @Test
    void createRevenueForecast() throws Exception {
        int databaseSizeBeforeCreate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        // Create the RevenueForecast
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);
        restRevenueForecastMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        RevenueForecast testRevenueForecast = revenueForecastList.get(revenueForecastList.size() - 1);
        assertThat(testRevenueForecast.getMonth()).isEqualTo(DEFAULT_MONTH);
        assertThat(testRevenueForecast.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testRevenueForecast.getUnitsSold()).isEqualTo(DEFAULT_UNITS_SOLD);
        assertThat(testRevenueForecast.getTotalRevenue()).isEqualTo(DEFAULT_TOTAL_REVENUE);
    }

    @Test
    void createRevenueForecastWithExistingId() throws Exception {
        // Create the RevenueForecast with an existing ID
        revenueForecast.setId("existing_id");
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);

        int databaseSizeBeforeCreate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRevenueForecastMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllRevenueForecasts() throws Exception {
        // Initialize the database
        revenueForecastRepository.save(revenueForecast);

        // Get all the revenueForecastList
        restRevenueForecastMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(revenueForecast.getId())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].unitsSold").value(hasItem(DEFAULT_UNITS_SOLD.doubleValue())))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(DEFAULT_TOTAL_REVENUE.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRevenueForecastsWithEagerRelationshipsIsEnabled() throws Exception {
        when(revenueForecastServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRevenueForecastMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(revenueForecastServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRevenueForecastsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(revenueForecastServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRevenueForecastMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(revenueForecastRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getRevenueForecast() throws Exception {
        // Initialize the database
        revenueForecastRepository.save(revenueForecast);

        // Get the revenueForecast
        restRevenueForecastMockMvc
            .perform(get(ENTITY_API_URL_ID, revenueForecast.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(revenueForecast.getId()))
            .andExpect(jsonPath("$.month").value(DEFAULT_MONTH))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.unitsSold").value(DEFAULT_UNITS_SOLD.doubleValue()))
            .andExpect(jsonPath("$.totalRevenue").value(DEFAULT_TOTAL_REVENUE.doubleValue()));
    }

    @Test
    void getNonExistingRevenueForecast() throws Exception {
        // Get the revenueForecast
        restRevenueForecastMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingRevenueForecast() throws Exception {
        // Initialize the database
        revenueForecastRepository.save(revenueForecast);

        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();
        revenueForecastSearchRepository.save(revenueForecast);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());

        // Update the revenueForecast
        RevenueForecast updatedRevenueForecast = revenueForecastRepository.findById(revenueForecast.getId()).get();
        updatedRevenueForecast.month(UPDATED_MONTH).year(UPDATED_YEAR).unitsSold(UPDATED_UNITS_SOLD).totalRevenue(UPDATED_TOTAL_REVENUE);
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(updatedRevenueForecast);

        restRevenueForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, revenueForecastDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isOk());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        RevenueForecast testRevenueForecast = revenueForecastList.get(revenueForecastList.size() - 1);
        assertThat(testRevenueForecast.getMonth()).isEqualTo(UPDATED_MONTH);
        assertThat(testRevenueForecast.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testRevenueForecast.getUnitsSold()).isEqualTo(UPDATED_UNITS_SOLD);
        assertThat(testRevenueForecast.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RevenueForecast> revenueForecastSearchList = IterableUtils.toList(revenueForecastSearchRepository.findAll());
                RevenueForecast testRevenueForecastSearch = revenueForecastSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRevenueForecastSearch.getMonth()).isEqualTo(UPDATED_MONTH);
                assertThat(testRevenueForecastSearch.getYear()).isEqualTo(UPDATED_YEAR);
                assertThat(testRevenueForecastSearch.getUnitsSold()).isEqualTo(UPDATED_UNITS_SOLD);
                assertThat(testRevenueForecastSearch.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
            });
    }

    @Test
    void putNonExistingRevenueForecast() throws Exception {
        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        revenueForecast.setId(UUID.randomUUID().toString());

        // Create the RevenueForecast
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRevenueForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, revenueForecastDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchRevenueForecast() throws Exception {
        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        revenueForecast.setId(UUID.randomUUID().toString());

        // Create the RevenueForecast
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRevenueForecastMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamRevenueForecast() throws Exception {
        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        revenueForecast.setId(UUID.randomUUID().toString());

        // Create the RevenueForecast
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRevenueForecastMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateRevenueForecastWithPatch() throws Exception {
        // Initialize the database
        revenueForecastRepository.save(revenueForecast);

        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();

        // Update the revenueForecast using partial update
        RevenueForecast partialUpdatedRevenueForecast = new RevenueForecast();
        partialUpdatedRevenueForecast.setId(revenueForecast.getId());

        partialUpdatedRevenueForecast.unitsSold(UPDATED_UNITS_SOLD).totalRevenue(UPDATED_TOTAL_REVENUE);

        restRevenueForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRevenueForecast.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRevenueForecast))
            )
            .andExpect(status().isOk());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        RevenueForecast testRevenueForecast = revenueForecastList.get(revenueForecastList.size() - 1);
        assertThat(testRevenueForecast.getMonth()).isEqualTo(DEFAULT_MONTH);
        assertThat(testRevenueForecast.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testRevenueForecast.getUnitsSold()).isEqualTo(UPDATED_UNITS_SOLD);
        assertThat(testRevenueForecast.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
    }

    @Test
    void fullUpdateRevenueForecastWithPatch() throws Exception {
        // Initialize the database
        revenueForecastRepository.save(revenueForecast);

        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();

        // Update the revenueForecast using partial update
        RevenueForecast partialUpdatedRevenueForecast = new RevenueForecast();
        partialUpdatedRevenueForecast.setId(revenueForecast.getId());

        partialUpdatedRevenueForecast
            .month(UPDATED_MONTH)
            .year(UPDATED_YEAR)
            .unitsSold(UPDATED_UNITS_SOLD)
            .totalRevenue(UPDATED_TOTAL_REVENUE);

        restRevenueForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRevenueForecast.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRevenueForecast))
            )
            .andExpect(status().isOk());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        RevenueForecast testRevenueForecast = revenueForecastList.get(revenueForecastList.size() - 1);
        assertThat(testRevenueForecast.getMonth()).isEqualTo(UPDATED_MONTH);
        assertThat(testRevenueForecast.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testRevenueForecast.getUnitsSold()).isEqualTo(UPDATED_UNITS_SOLD);
        assertThat(testRevenueForecast.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
    }

    @Test
    void patchNonExistingRevenueForecast() throws Exception {
        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        revenueForecast.setId(UUID.randomUUID().toString());

        // Create the RevenueForecast
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRevenueForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, revenueForecastDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchRevenueForecast() throws Exception {
        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        revenueForecast.setId(UUID.randomUUID().toString());

        // Create the RevenueForecast
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRevenueForecastMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamRevenueForecast() throws Exception {
        int databaseSizeBeforeUpdate = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        revenueForecast.setId(UUID.randomUUID().toString());

        // Create the RevenueForecast
        RevenueForecastDTO revenueForecastDTO = revenueForecastMapper.toDto(revenueForecast);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRevenueForecastMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(revenueForecastDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RevenueForecast in the database
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteRevenueForecast() throws Exception {
        // Initialize the database
        revenueForecastRepository.save(revenueForecast);
        revenueForecastRepository.save(revenueForecast);
        revenueForecastSearchRepository.save(revenueForecast);

        int databaseSizeBeforeDelete = revenueForecastRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the revenueForecast
        restRevenueForecastMockMvc
            .perform(delete(ENTITY_API_URL_ID, revenueForecast.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RevenueForecast> revenueForecastList = revenueForecastRepository.findAll();
        assertThat(revenueForecastList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(revenueForecastSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchRevenueForecast() throws Exception {
        // Initialize the database
        revenueForecast = revenueForecastRepository.save(revenueForecast);
        revenueForecastSearchRepository.save(revenueForecast);

        // Search the revenueForecast
        restRevenueForecastMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + revenueForecast.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(revenueForecast.getId())))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH)))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].unitsSold").value(hasItem(DEFAULT_UNITS_SOLD.doubleValue())))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(DEFAULT_TOTAL_REVENUE.doubleValue())));
    }
}
