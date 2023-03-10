package co.empathy.academy.search.Configuration;

import co.empathy.academy.search.Service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Bean
    public SearchService searchService(ElasticEngineImpl searchEngine) {
        return new SearchServiceImpl(searchEngine);
    }

    @Bean
    public UserService userService(UserEngineImpl userEngine) {return new UserServiceImpl(userEngine);}
}
