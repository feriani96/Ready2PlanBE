package com.readytoplanbe.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.readytoplanbe.app.domain.FinancialForecast;
import com.readytoplanbe.app.repository.FinancialForecastRepository;
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
 * Spring Data Elasticsearch repository for the {@link FinancialForecast} entity.
 */
public interface FinancialForecastSearchRepository
    extends ElasticsearchRepository<FinancialForecast, String>, FinancialForecastSearchRepositoryInternal {}

interface FinancialForecastSearchRepositoryInternal {
    Stream<FinancialForecast> search(String query);

    Stream<FinancialForecast> search(Query query);

    void index(FinancialForecast entity);
}

class FinancialForecastSearchRepositoryInternalImpl implements FinancialForecastSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final FinancialForecastRepository repository;

    FinancialForecastSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, FinancialForecastRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<FinancialForecast> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<FinancialForecast> search(Query query) {
        return elasticsearchTemplate.search(query, FinancialForecast.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(FinancialForecast entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
