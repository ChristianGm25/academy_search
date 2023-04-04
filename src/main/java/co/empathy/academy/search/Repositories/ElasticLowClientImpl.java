package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import co.empathy.academy.search.Model.Movie;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void indexCreation(){
        try {
            final String assetJsonSource = "src/main/java/co/empathy/academy/search/Configuration/my_index_settings.json";
            InputStream input = new FileInputStream(assetJsonSource);
            CreateIndexResponse request = elasticSearchConfiguration.elasticsearchClient().indices().create(f ->
                    f.index(indexName).withJson(input));
        } catch (IOException e) {
            System.out.println("Failed to create an index");
        }
    }

    public void indexDeletion(){
        try {
            DeleteIndexResponse request = elasticSearchConfiguration.elasticsearchClient().indices().delete(f ->
                    f.index(indexName));
        } catch (IOException e) {
            System.out.println("Failed to create an index");
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