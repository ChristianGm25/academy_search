package co.empathy.academy.search.Repositories;

public class QueryEngineImpl {
    private ElasticLowClientImpl elasticLowClient;
    private static final String indexName = "movies";

    public QueryEngineImpl(ElasticLowClientImpl elasticLowClient) {
        this.elasticLowClient = elasticLowClient;
    }

}
