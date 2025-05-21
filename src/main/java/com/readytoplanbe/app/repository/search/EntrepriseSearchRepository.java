package com.readytoplanbe.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.readytoplanbe.app.domain.Entreprise;
import com.readytoplanbe.app.repository.EntrepriseRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Entreprise} entity.
 */
public interface EntrepriseSearchRepository extends ElasticsearchRepository<Entreprise, String>, EntrepriseSearchRepositoryInternal {}

interface EntrepriseSearchRepositoryInternal {
    Stream<Entreprise> search(String query);

    Stream<Entreprise> search(Query query);

    void index(Entreprise entity);
}

class EntrepriseSearchRepositoryInternalImpl implements EntrepriseSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final EntrepriseRepository repository;

    EntrepriseSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, EntrepriseRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Entreprise> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Entreprise> search(Query query) {
        return elasticsearchTemplate.search(query, Entreprise.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Entreprise entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
