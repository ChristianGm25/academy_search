package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.empathy.academy.search.Model.Movie;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class QueryEngineImpl {

    private final String NOT_MOVIES = "tvEpisode, video, videoGame, tvPilot";
    private final ElasticsearchClient elasticsearchClient;

    private static final String indexName = "movies";

    public QueryEngineImpl(ElasticsearchClient elasticClient) {
        this.elasticsearchClient = elasticClient;
    }


    public List<Movie> getDocuments() {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("startYear").order(SortOrder.Desc)).build();
        List<Query> queries = new LinkedList<>();
        Query beforeThisYear = RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery();
        queries.add(beforeThisYear);
        return performQuery(queries, sort, 50);
    }

    public List<Movie> getDocumentsQuery(String query) {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("startYear").order(SortOrder.Desc)).build();
        List<Query> queries = new LinkedList<>();

        //Queries
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(p -> p.fields("primaryTitle", "originalTitle").query(query));
        Query beforeThisYear = RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery();
        queries.add(multiMatchQuery._toQuery());
        queries.add(beforeThisYear);

        return performQuery(queries, sort, 50);
    }


    public List<Movie> getDocumentsGenre(String genre) {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("averageRating").order(SortOrder.Desc)).build();

        CommonTermsQuery genreQuery = CommonTermsQuery.of(p -> p.field("genres").query(genre));
        List<Query> queries = new LinkedList<>();
        Query beforeThisYear = RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery();
        queries.add(genreQuery._toQuery());
        queries.add(beforeThisYear);

        return performQuery(queries, sort, 50);
    }


    public List<Movie> performQuery(List<Query> queries, SortOptions sort, int size) {
        List<Movie> movies = new LinkedList<>();
        //Query bulkQueries = BoolQuery.of(p->p.filter(queries).mustNot(MatchQuery.of(f->f.field("titleType").query(NOT_MOVIES))._toQuery()))._toQuery();
        queries.add(MatchQuery.of(p -> p.field("titleType").query("movie"))._toQuery());
        Query bulkQueries = BoolQuery.of(p -> p.filter(queries))._toQuery();
        SearchRequest searchRequest = SearchRequest.of(p -> p
                .index(indexName)
                .query(bulkQueries)
                .sort(sort)
                .size(size));
        try {
            SearchResponse searchResponse = elasticsearchClient.search(searchRequest, Movie.class);

            List<Hit<Movie>> hits = searchResponse.hits().hits();

            for (Hit object : hits) {
                movies.add((Movie) object.source());
            }
        } catch (IOException e) {
            System.out.println("There was an error retrieving the movies");
        }
        return movies;
    }


}
