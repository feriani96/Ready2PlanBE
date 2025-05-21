package com.readytoplanbe.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.readytoplanbe.app.domain.RevenueForecast;
import com.readytoplanbe.app.repository.RevenueForecastRepository;
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
 * Spring Data Elasticsearch repository for the {@link RevenueForecast} entity.
 */
public interface RevenueForecastSearchRepository
    extends ElasticsearchRepository<RevenueForecast, String>, RevenueForecastSearchRepositoryInternal {}

interface RevenueForecastSearchRepositoryInternal {
    Stream<RevenueForecast> search(String query);

    Stream<RevenueForecast> search(Query query);

    void index(RevenueForecast entity);
}

class RevenueForecastSearchRepositoryInternalImpl implements RevenueForecastSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final RevenueForecastRepository repository;

    RevenueForecastSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, RevenueForecastRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<RevenueForecast> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<RevenueForecast> search(Query query) {
        return elasticsearchTemplate.search(query, RevenueForecast.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(RevenueForecast entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
