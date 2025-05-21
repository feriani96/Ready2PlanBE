package com.readytoplanbe.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.readytoplanbe.app.IntegrationTest;
import com.readytoplanbe.app.domain.Entreprise;
import com.readytoplanbe.app.repository.EntrepriseRepository;
import com.readytoplanbe.app.repository.search.EntrepriseSearchRepository;
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
 * Integration tests for the {@link EntrepriseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EntrepriseResourceIT {

    private static final String DEFAULT_NOM_ETP = "AAAAAAAAAA";
    private static final String UPDATED_NOM_ETP = "BBBBBBBBBB";

    private static final String DEFAULT_PAYS = "AAAAAAAAAA";
    private static final String UPDATED_PAYS = "BBBBBBBBBB";

    private static final Long DEFAULT_TELEPHONE = 1L;
    private static final Long UPDATED_TELEPHONE = 2L;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_DEVISE_SIZE = 1L;
    private static final Long UPDATED_DEVISE_SIZE = 2L;

    private static final String DEFAULT_DEVISE = "AAAAAAAAAA";
    private static final String UPDATED_DEVISE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/entreprises";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/entreprises";

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private EntrepriseSearchRepository entrepriseSearchRepository;

    @Autowired
    private MockMvc restEntrepriseMockMvc;

    private Entreprise entreprise;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Entreprise createEntity() {
        Entreprise entreprise = new Entreprise()
            .nom_etp(DEFAULT_NOM_ETP)
            .pays(DEFAULT_PAYS)
            .telephone(DEFAULT_TELEPHONE)
            .description(DEFAULT_DESCRIPTION)
            .deviseSize(DEFAULT_DEVISE_SIZE)
            .devise(DEFAULT_DEVISE);
        return entreprise;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Entreprise createUpdatedEntity() {
        Entreprise entreprise = new Entreprise()
            .nom_etp(UPDATED_NOM_ETP)
            .pays(UPDATED_PAYS)
            .telephone(UPDATED_TELEPHONE)
            .description(UPDATED_DESCRIPTION)
            .deviseSize(UPDATED_DEVISE_SIZE)
            .devise(UPDATED_DEVISE);
        return entreprise;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        entrepriseSearchRepository.deleteAll();
        assertThat(entrepriseSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        entrepriseRepository.deleteAll();
        entreprise = createEntity();
    }

    @Test
    void createEntreprise() throws Exception {
        int databaseSizeBeforeCreate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        // Create the Entreprise
        restEntrepriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(entreprise)))
            .andExpect(status().isCreated());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Entreprise testEntreprise = entrepriseList.get(entrepriseList.size() - 1);
        assertThat(testEntreprise.getNom_etp()).isEqualTo(DEFAULT_NOM_ETP);
        assertThat(testEntreprise.getPays()).isEqualTo(DEFAULT_PAYS);
        assertThat(testEntreprise.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testEntreprise.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEntreprise.getDeviseSize()).isEqualTo(DEFAULT_DEVISE_SIZE);
        assertThat(testEntreprise.getDevise()).isEqualTo(DEFAULT_DEVISE);
    }

    @Test
    void createEntrepriseWithExistingId() throws Exception {
        // Create the Entreprise with an existing ID
        entreprise.setId("existing_id");

        int databaseSizeBeforeCreate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntrepriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(entreprise)))
            .andExpect(status().isBadRequest());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEntreprises() throws Exception {
        // Initialize the database
        entrepriseRepository.save(entreprise);

        // Get all the entrepriseList
        restEntrepriseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entreprise.getId())))
            .andExpect(jsonPath("$.[*].nom_etp").value(hasItem(DEFAULT_NOM_ETP)))
            .andExpect(jsonPath("$.[*].pays").value(hasItem(DEFAULT_PAYS)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deviseSize").value(hasItem(DEFAULT_DEVISE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].devise").value(hasItem(DEFAULT_DEVISE)));
    }

    @Test
    void getEntreprise() throws Exception {
        // Initialize the database
        entrepriseRepository.save(entreprise);

        // Get the entreprise
        restEntrepriseMockMvc
            .perform(get(ENTITY_API_URL_ID, entreprise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(entreprise.getId()))
            .andExpect(jsonPath("$.nom_etp").value(DEFAULT_NOM_ETP))
            .andExpect(jsonPath("$.pays").value(DEFAULT_PAYS))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.deviseSize").value(DEFAULT_DEVISE_SIZE.intValue()))
            .andExpect(jsonPath("$.devise").value(DEFAULT_DEVISE));
    }

    @Test
    void getNonExistingEntreprise() throws Exception {
        // Get the entreprise
        restEntrepriseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingEntreprise() throws Exception {
        // Initialize the database
        entrepriseRepository.save(entreprise);

        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();
        entrepriseSearchRepository.save(entreprise);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());

        // Update the entreprise
        Entreprise updatedEntreprise = entrepriseRepository.findById(entreprise.getId()).get();
        updatedEntreprise
            .nom_etp(UPDATED_NOM_ETP)
            .pays(UPDATED_PAYS)
            .telephone(UPDATED_TELEPHONE)
            .description(UPDATED_DESCRIPTION)
            .deviseSize(UPDATED_DEVISE_SIZE)
            .devise(UPDATED_DEVISE);

        restEntrepriseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEntreprise.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEntreprise))
            )
            .andExpect(status().isOk());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        Entreprise testEntreprise = entrepriseList.get(entrepriseList.size() - 1);
        assertThat(testEntreprise.getNom_etp()).isEqualTo(UPDATED_NOM_ETP);
        assertThat(testEntreprise.getPays()).isEqualTo(UPDATED_PAYS);
        assertThat(testEntreprise.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testEntreprise.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEntreprise.getDeviseSize()).isEqualTo(UPDATED_DEVISE_SIZE);
        assertThat(testEntreprise.getDevise()).isEqualTo(UPDATED_DEVISE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Entreprise> entrepriseSearchList = IterableUtils.toList(entrepriseSearchRepository.findAll());
                Entreprise testEntrepriseSearch = entrepriseSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEntrepriseSearch.getNom_etp()).isEqualTo(UPDATED_NOM_ETP);
                assertThat(testEntrepriseSearch.getPays()).isEqualTo(UPDATED_PAYS);
                assertThat(testEntrepriseSearch.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
                assertThat(testEntrepriseSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testEntrepriseSearch.getDeviseSize()).isEqualTo(UPDATED_DEVISE_SIZE);
                assertThat(testEntrepriseSearch.getDevise()).isEqualTo(UPDATED_DEVISE);
            });
    }

    @Test
    void putNonExistingEntreprise() throws Exception {
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        entreprise.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntrepriseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, entreprise.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(entreprise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEntreprise() throws Exception {
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        entreprise.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntrepriseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(entreprise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEntreprise() throws Exception {
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        entreprise.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntrepriseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(entreprise)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEntrepriseWithPatch() throws Exception {
        // Initialize the database
        entrepriseRepository.save(entreprise);

        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();

        // Update the entreprise using partial update
        Entreprise partialUpdatedEntreprise = new Entreprise();
        partialUpdatedEntreprise.setId(entreprise.getId());

        partialUpdatedEntreprise.nom_etp(UPDATED_NOM_ETP).pays(UPDATED_PAYS).telephone(UPDATED_TELEPHONE);

        restEntrepriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntreprise.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEntreprise))
            )
            .andExpect(status().isOk());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        Entreprise testEntreprise = entrepriseList.get(entrepriseList.size() - 1);
        assertThat(testEntreprise.getNom_etp()).isEqualTo(UPDATED_NOM_ETP);
        assertThat(testEntreprise.getPays()).isEqualTo(UPDATED_PAYS);
        assertThat(testEntreprise.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testEntreprise.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEntreprise.getDeviseSize()).isEqualTo(DEFAULT_DEVISE_SIZE);
        assertThat(testEntreprise.getDevise()).isEqualTo(DEFAULT_DEVISE);
    }

    @Test
    void fullUpdateEntrepriseWithPatch() throws Exception {
        // Initialize the database
        entrepriseRepository.save(entreprise);

        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();

        // Update the entreprise using partial update
        Entreprise partialUpdatedEntreprise = new Entreprise();
        partialUpdatedEntreprise.setId(entreprise.getId());

        partialUpdatedEntreprise
            .nom_etp(UPDATED_NOM_ETP)
            .pays(UPDATED_PAYS)
            .telephone(UPDATED_TELEPHONE)
            .description(UPDATED_DESCRIPTION)
            .deviseSize(UPDATED_DEVISE_SIZE)
            .devise(UPDATED_DEVISE);

        restEntrepriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntreprise.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEntreprise))
            )
            .andExpect(status().isOk());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        Entreprise testEntreprise = entrepriseList.get(entrepriseList.size() - 1);
        assertThat(testEntreprise.getNom_etp()).isEqualTo(UPDATED_NOM_ETP);
        assertThat(testEntreprise.getPays()).isEqualTo(UPDATED_PAYS);
        assertThat(testEntreprise.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testEntreprise.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEntreprise.getDeviseSize()).isEqualTo(UPDATED_DEVISE_SIZE);
        assertThat(testEntreprise.getDevise()).isEqualTo(UPDATED_DEVISE);
    }

    @Test
    void patchNonExistingEntreprise() throws Exception {
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        entreprise.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntrepriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, entreprise.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(entreprise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEntreprise() throws Exception {
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        entreprise.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntrepriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(entreprise))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEntreprise() throws Exception {
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        entreprise.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntrepriseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(entreprise))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEntreprise() throws Exception {
        // Initialize the database
        entrepriseRepository.save(entreprise);
        entrepriseRepository.save(entreprise);
        entrepriseSearchRepository.save(entreprise);

        int databaseSizeBeforeDelete = entrepriseRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the entreprise
        restEntrepriseMockMvc
            .perform(delete(ENTITY_API_URL_ID, entreprise.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entrepriseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEntreprise() throws Exception {
        // Initialize the database
        entreprise = entrepriseRepository.save(entreprise);
        entrepriseSearchRepository.save(entreprise);

        // Search the entreprise
        restEntrepriseMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + entreprise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entreprise.getId())))
            .andExpect(jsonPath("$.[*].nom_etp").value(hasItem(DEFAULT_NOM_ETP)))
            .andExpect(jsonPath("$.[*].pays").value(hasItem(DEFAULT_PAYS)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deviseSize").value(hasItem(DEFAULT_DEVISE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].devise").value(hasItem(DEFAULT_DEVISE)));
    }
}
