package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.JsonData;
import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import co.empathy.academy.search.Model.Movie;
import org.springframework.stereotype.Component;

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
    public List<Movie> getDocumentsQuery(String query) {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("startYear").order(SortOrder.Desc)).build();

        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(p -> p.fields("primaryTitle", "originalTitle").query(query));
        List<Movie> movies = new LinkedList<>();
        List<Query> queries = new LinkedList<>();
        Query beforeThisYear = RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery();
        Query typeQuery = MatchQuery.of(p -> p.query("movie").field("titleType"))._toQuery();
        queries.add(typeQuery);
        queries.add(multiMatchQuery._toQuery());
        queries.add(beforeThisYear);

        Query bulkQueries = BoolQuery.of(p -> p.filter(queries))._toQuery();
        SearchRequest searchRequest = SearchRequest.of(p -> p
                .index(indexName)
                .query(bulkQueries)
                .sort(sort)
                .size(50));
        try {
            SearchResponse searchResponse = elasticSearchConfiguration.elasticsearchClient().search(searchRequest, Movie.class);

            List<Hit<Movie>> hits = searchResponse.hits().hits();

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
    public List<Movie> getDocuments() {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("startYear").order(SortOrder.Desc)).build();
        List<Query> queries = new LinkedList<>();
        Query beforeThisYear = RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery();
        queries.add(beforeThisYear);
        Query typeQuery = MatchQuery.of(p -> p.query("movie").field("titleType"))._toQuery();
        queries.add(typeQuery);
        Query bulkQueries = BoolQuery.of(p -> p.filter(queries))._toQuery();
        SearchRequest searchRequest = SearchRequest.of(p -> p
                .index(indexName)
                .query(bulkQueries)
                .sort(sort)
                .size(50));

        List<Movie> movies = new LinkedList<>();
        try {
            SearchResponse searchResponse = elasticSearchConfiguration
                    .elasticsearchClient().search(searchRequest,
                            Movie.class);
            List<Hit<Movie>> hits = searchResponse.hits().hits();

            for (Hit<Movie> movie : hits) {
                movies.add(movie.source());
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
            InputStream mapping = getClass().getClassLoader().getResourceAsStream("my_index_mapping.json");
            InputStream analyzer = getClass().getClassLoader().getResourceAsStream("custom_analyzer.json");

            //Create the index
            elasticSearchConfiguration.elasticsearchClient().indices().create(f -> f.index(indexName));

            //Close it and add the mapping
            elasticSearchConfiguration.elasticsearchClient().indices().close(f -> f.index(indexName));
            elasticSearchConfiguration.elasticsearchClient().indices().putMapping(f -> f.index(indexName).withJson(mapping));
            //Add the analyzer
            elasticSearchConfiguration.elasticsearchClient().indices().putSettings(f -> f.index(indexName).withJson(analyzer));
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