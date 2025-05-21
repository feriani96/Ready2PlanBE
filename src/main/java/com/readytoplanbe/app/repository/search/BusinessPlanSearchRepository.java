package com.readytoplanbe.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.readytoplanbe.app.domain.BusinessPlan;
import com.readytoplanbe.app.repository.BusinessPlanRepository;
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
 * Spring Data Elasticsearch repository for the {@link BusinessPlan} entity.
 */
public interface BusinessPlanSearchRepository extends ElasticsearchRepository<BusinessPlan, String>, BusinessPlanSearchRepositoryInternal {}

interface BusinessPlanSearchRepositoryInternal {
    Stream<BusinessPlan> search(String query);

    Stream<BusinessPlan> search(Query query);

    void index(BusinessPlan entity);
}

class BusinessPlanSearchRepositoryInternalImpl implements BusinessPlanSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final BusinessPlanRepository repository;

    BusinessPlanSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, BusinessPlanRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<BusinessPlan> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<BusinessPlan> search(Query query) {
        return elasticsearchTemplate.search(query, BusinessPlan.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(BusinessPlan entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
