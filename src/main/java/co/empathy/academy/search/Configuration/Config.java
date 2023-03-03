package co.empathy.academy.search.Configuration;

import co.empathy.academy.search.Service.ElasticEngine;
import co.empathy.academy.search.Service.ElasticEngineImpl;
import co.empathy.academy.search.Service.SearchService;
import co.empathy.academy.search.Service.SearchServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Bean
    public SearchService searchService(ElasticEngineImpl searchEngine) {
        return new SearchServiceImpl(searchEngine);
    }
}
