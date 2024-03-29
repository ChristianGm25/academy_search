package co.empathy.academy.search.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import co.empathy.academy.search.Repositories.QueryEngineImpl;
import co.empathy.academy.search.Service.IndexService;
import co.empathy.academy.search.Service.IndexServiceImpl;
import co.empathy.academy.search.Service.SearchService;
import co.empathy.academy.search.Service.SearchServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class Config implements AsyncConfigurer {


    @Bean
    public SearchService searchService(ElasticLowClientImpl searchEngine) {
        return new SearchServiceImpl(searchEngine);
    }


    @Bean
    public IndexService indexService(ElasticLowClientImpl elasticLowClient) {
        return new IndexServiceImpl(elasticLowClient);
    }

    @Bean
    public QueryEngineImpl queryEngine(ElasticsearchClient elasticsearchClient) {
        return new QueryEngineImpl(elasticsearchClient);
    }

}
