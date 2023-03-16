package co.empathy.academy.search.Configuration;

import co.empathy.academy.search.Service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class Config implements AsyncConfigurer {


    @Bean
    public SearchService searchService(ElasticEngineImpl searchEngine) {
        return new SearchServiceImpl(searchEngine);
    }

    @Bean
    public UserEngine userEngine (){
        return new UserEngineImpl();
    }
}
