package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import co.empathy.academy.search.Model.Movie;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
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
    public List<Movie> getDocuments() {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("startYear").order(SortOrder.Desc)).build();
        SearchRequest searchRequest = SearchRequest.of(p -> p
                .index(indexName)
                .size(50));

        List<Movie> movies = new LinkedList<>();
        try {
            SearchResponse searchResponse = elasticSearchConfiguration
                    .elasticsearchClient().search(searchRequest,
                            Movie.class);
            List<Hit> hits = searchResponse.hits().hits();

            for (Hit object : hits) {
                movies.add((Movie) object.source());
            }
            return movies;
        } catch (IOException e) {
            System.out.println("There was an error retrieving the movies");
        }
        return movies;
    }


    @Override
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