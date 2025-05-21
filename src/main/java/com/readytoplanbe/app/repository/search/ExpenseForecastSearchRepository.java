package com.readytoplanbe.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.readytoplanbe.app.domain.ExpenseForecast;
import com.readytoplanbe.app.repository.ExpenseForecastRepository;
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
 * Spring Data Elasticsearch repository for the {@link ExpenseForecast} entity.
 */
public interface ExpenseForecastSearchRepository
    extends ElasticsearchRepository<ExpenseForecast, String>, ExpenseForecastSearchRepositoryInternal {}

interface ExpenseForecastSearchRepositoryInternal {
    Stream<ExpenseForecast> search(String query);

    Stream<ExpenseForecast> search(Query query);

    void index(ExpenseForecast entity);
}

class ExpenseForecastSearchRepositoryInternalImpl implements ExpenseForecastSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ExpenseForecastRepository repository;

    ExpenseForecastSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ExpenseForecastRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ExpenseForecast> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<ExpenseForecast> search(Query query) {
        return elasticsearchTemplate.search(query, ExpenseForecast.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ExpenseForecast entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
