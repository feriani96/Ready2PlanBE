package com.readytoplanbe.app.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.readytoplanbe.app.domain.Entreprise;
import com.readytoplanbe.app.repository.EntrepriseRepository;
import com.readytoplanbe.app.repository.search.EntrepriseSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Entreprise}.
 */
@Service
public class EntrepriseService {

    private final Logger log = LoggerFactory.getLogger(EntrepriseService.class);

    private final EntrepriseRepository entrepriseRepository;

    private final EntrepriseSearchRepository entrepriseSearchRepository;

    public EntrepriseService(EntrepriseRepository entrepriseRepository, EntrepriseSearchRepository entrepriseSearchRepository) {
        this.entrepriseRepository = entrepriseRepository;
        this.entrepriseSearchRepository = entrepriseSearchRepository;
    }

    /**
     * Save a entreprise.
     *
     * @param entreprise the entity to save.
     * @return the persisted entity.
     */
    public Entreprise save(Entreprise entreprise) {
        log.debug("Request to save Entreprise : {}", entreprise);
        Entreprise result = entrepriseRepository.save(entreprise);
        entrepriseSearchRepository.index(result);
        return result;
    }

    /**
     * Update a entreprise.
     *
     * @param entreprise the entity to save.
     * @return the persisted entity.
     */
    public Entreprise update(Entreprise entreprise) {
        log.debug("Request to update Entreprise : {}", entreprise);
        Entreprise result = entrepriseRepository.save(entreprise);
        entrepriseSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a entreprise.
     *
     * @param entreprise the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Entreprise> partialUpdate(Entreprise entreprise) {
        log.debug("Request to partially update Entreprise : {}", entreprise);

        return entrepriseRepository
            .findById(entreprise.getId())
            .map(existingEntreprise -> {
                if (entreprise.getNom_etp() != null) {
                    existingEntreprise.setNom_etp(entreprise.getNom_etp());
                }
                if (entreprise.getPays() != null) {
                    existingEntreprise.setPays(entreprise.getPays());
                }
                if (entreprise.getTelephone() != null) {
                    existingEntreprise.setTelephone(entreprise.getTelephone());
                }
                if (entreprise.getDescription() != null) {
                    existingEntreprise.setDescription(entreprise.getDescription());
                }
                if (entreprise.getDeviseSize() != null) {
                    existingEntreprise.setDeviseSize(entreprise.getDeviseSize());
                }
                if (entreprise.getDevise() != null) {
                    existingEntreprise.setDevise(entreprise.getDevise());
                }

                return existingEntreprise;
            })
            .map(entrepriseRepository::save)
            .map(savedEntreprise -> {
                entrepriseSearchRepository.save(savedEntreprise);

                return savedEntreprise;
            });
    }

    /**
     * Get all the entreprises.
     *
     * @return the list of entities.
     */
    public List<Entreprise> findAll() {
        log.debug("Request to get all Entreprises");
        return entrepriseRepository.findAll();
    }

    /**
     * Get one entreprise by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Entreprise> findOne(String id) {
        log.debug("Request to get Entreprise : {}", id);
        return entrepriseRepository.findById(id);
    }

    /**
     * Delete the entreprise by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Entreprise : {}", id);
        entrepriseRepository.deleteById(id);
        entrepriseSearchRepository.deleteById(id);
    }

    /**
     * Search for the entreprise corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    public List<Entreprise> search(String query) {
        log.debug("Request to search Entreprises for query {}", query);
        return StreamSupport.stream(entrepriseSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
