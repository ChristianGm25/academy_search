package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import co.empathy.academy.search.Model.Movie;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ElasticLowClientImpl implements ElasticLowClient {

    private static final String indexName = "movies";
    private final ElasticSearchConfiguration elasticSearchConfiguration;

    public ElasticLowClientImpl(ElasticSearchConfiguration elasticSearchConfiguration) {
        this.elasticSearchConfiguration = elasticSearchConfiguration;
    }


    @Override
    public String search() {
        try {
            return elasticSearchConfiguration.elasticsearchClient().cluster().health().clusterName();
        } catch (IOException e) {
            return "Error retrieving cluster name";
        }
    }


    @Override
    public void indexCreation() throws IOException {
        try {
            InputStream mapping = getClass().getClassLoader().getResourceAsStream("my_index_mapping.json");
            InputStream analyzer = getClass().getClassLoader().getResourceAsStream("custom_analyzer.json");

            //Create the index
            elasticSearchConfiguration.elasticsearchClient().indices().create(f -> f.index(indexName));

            //Close it and add the mapping
            elasticSearchConfiguration.elasticsearchClient().indices().close(f -> f.index(indexName));
            elasticSearchConfiguration.elasticsearchClient().indices().putSettings(f -> f.index(indexName).withJson(analyzer));
            //Add the analyzer
            elasticSearchConfiguration.elasticsearchClient().indices().putMapping(f -> f.index(indexName).withJson(mapping));
            //Open the index again
            elasticSearchConfiguration.elasticsearchClient().indices().open(f -> f.index(indexName));

        } catch (IOException e) {
            throw new IOException("Failed to create an index (Elastic not running)");
        }
    }

    @Override
    public void indexDeletion() throws IOException {
        try {
            DeleteIndexResponse request = elasticSearchConfiguration.elasticsearchClient().indices().delete(f ->
                    f.index(indexName));
        } catch (IOException e) {
            throw new IOException("Failed to delete an index (Non existing or elastic not running)");
        }
    }

    @Override
    public void indexMovies(List<Movie> movies) throws IOException{
        BulkRequest.Builder builder = new BulkRequest.Builder();
        if(movies.isEmpty()){
            return;
        }
        for(Movie m: movies){
            builder.operations(f ->
                    f.index(idx -> idx.index(indexName)
                            .document(m)));
        }
        BulkResponse response = elasticSearchConfiguration.elasticsearchClient().bulk(builder.build());

        if(response.errors()){
            System.out.print("Error indexing");
        }
        //LOGGER.info("Indexing multiple docs");


    }


}