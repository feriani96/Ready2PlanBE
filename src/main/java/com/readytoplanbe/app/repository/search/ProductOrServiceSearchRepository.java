package com.readytoplanbe.app.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.readytoplanbe.app.domain.ProductOrService;
import com.readytoplanbe.app.repository.ProductOrServiceRepository;
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
 * Spring Data Elasticsearch repository for the {@link ProductOrService} entity.
 */
public interface ProductOrServiceSearchRepository
    extends ElasticsearchRepository<ProductOrService, String>, ProductOrServiceSearchRepositoryInternal {}

interface ProductOrServiceSearchRepositoryInternal {
    Stream<ProductOrService> search(String query);

    Stream<ProductOrService> search(Query query);

    void index(ProductOrService entity);
}

class ProductOrServiceSearchRepositoryInternalImpl implements ProductOrServiceSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ProductOrServiceRepository repository;

    ProductOrServiceSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ProductOrServiceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ProductOrService> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<ProductOrService> search(Query query) {
        return elasticsearchTemplate.search(query, ProductOrService.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ProductOrService entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
