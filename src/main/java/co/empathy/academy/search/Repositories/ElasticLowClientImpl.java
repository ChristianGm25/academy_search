package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import co.empathy.academy.search.Model.Movie;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ElasticLowClientImpl implements ElasticLowClient {

    private static final String indexName = "movies";
    private final ElasticSearchConfiguration elasticSearchConfiguration;

    public ElasticLowClientImpl(ElasticSearchConfiguration elasticSearchConfiguration){
        this.elasticSearchConfiguration = elasticSearchConfiguration;
    }

    @Override
    public String search() throws IOException {
        return elasticSearchConfiguration.elasticsearchClient().cluster().health().clusterName();
    }

    public void indexCreation() throws IOException {
        try {
            final String assetJsonSource = "src/main/java/co/empathy/academy/search/Configuration/my_index_settings.json";
            InputStream input = new FileInputStream(assetJsonSource);
            CreateIndexResponse request = elasticSearchConfiguration.elasticsearchClient().indices().create(f ->
                    f.index(indexName).withJson(input));
        } catch (IOException e) {
            throw new IOException("Failed to create an index (Elastic not running)");
        }
    }

    public void indexDeletion() throws IOException {
        try {
            DeleteIndexResponse request = elasticSearchConfiguration.elasticsearchClient().indices().delete(f ->
                    f.index(indexName));
        } catch (IOException e) {
            throw new IOException("Failed to delete an index (Non existing or elastic not running)");
        }
    }

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