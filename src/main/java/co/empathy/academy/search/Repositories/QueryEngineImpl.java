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
import java.util.*;

public class QueryEngineImpl implements QueryEngine {

    //Filter to not print these types of docs
    private final String NOT_MOVIES = "tvEpisode, video, videoGame, tvPilot";
    private final ElasticsearchClient elasticsearchClient;

    private static final String indexName = "movies";

    public QueryEngineImpl(ElasticsearchClient elasticClient) {
        this.elasticsearchClient = elasticClient;
    }


    /**
     * Return a list of movies that depends on the selectedMovies parameter,
     * calculating which genre appears the most in the list and returning a query
     * over that genre.
     *
     * @param selectedMovies, list containing the movies selected in the front
     * @return the list of movies recommended
     */
    @Override
    public List<Movie> getRecommendedMovies(List<Movie> selectedMovies) {
        Map<String, Integer> genresMap = new HashMap<>();
        int tempValue;
        String genreToQuery = "";
        for (Movie m : selectedMovies) {
            for (String genre : m.getGenres()) {
                if (genresMap.containsKey(genre)) {
                    tempValue = genresMap.get(genre) + 1;
                    genresMap.put(genre, tempValue);
                } else {
                    genresMap.put(genre, 1);
                }
            }
        }

        tempValue = 0;
        //Iterate over the map and find the genre with the most appearances
        for (String key : genresMap.keySet()) {
            if (tempValue <= genresMap.get(key)) {
                tempValue = genresMap.get(key);
                genreToQuery = key;
            }
        }
        if (genreToQuery.equals("")) {
            return getDocuments();
        }
        return getDocumentsGenre(genreToQuery, 10);
    }

    /**
     * Return a list of movies that depend on the filters passed as parameters
     *
     * @param query,       words to perform a match query over
     * @param genre,       genre of the result movies
     * @param minDuration, minimum duration in minutes of the result movies
     * @param maxDuration, maximum duration in minutes of the result movies
     * @param minDate,     minimum date of the result movies
     * @param maxDate,     maximum date of the result movies
     * @param minScore,    minimum score of the result movies (Max is always 10)
     * @return the list of movies recommended that match the filters
     */
    @Override
    public List<Movie> getDocumentsFiltered(String query, Optional<String> genre, Optional<Integer> minDuration,
                                            Optional<Integer> maxDuration, Optional<Integer> minDate,
                                            Optional<Integer> maxDate, Optional<Double> minScore) {

        //Sort options for the results
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("startYear").order(SortOrder.Desc)).build();
        //List of queries
        List<Query> queries = new LinkedList<>();

        //Always add the MatchQuery on the field query
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(p -> p.fields("primaryTitle", "originalTitle").query(query));
        queries.add(multiMatchQuery._toQuery());

        //Check every argument and, if present, add a query to filter it
        if (genre.isPresent()) {
            String[] terms = genre.get().split(",");
            for (String term : terms) {
                CommonTermsQuery genreQuery = CommonTermsQuery.of(p -> p.field("genres").query(term));
                queries.add(genreQuery._toQuery());
            }
        }
        if (minDuration.isPresent()) {
            queries.add(RangeQuery.of(p -> p.field("runtimeMinutes").gte(JsonData.of(minDuration.get())))._toQuery());
        }
        if (maxDuration.isPresent()) {
            queries.add(RangeQuery.of(p -> p.field("runtimeMinutes").lte(JsonData.of(maxDuration.get())))._toQuery());
        }
        if (minDate.isPresent()) {
            queries.add(RangeQuery.of(p -> p.field("startYear").gte(JsonData.of(minDate.get())))._toQuery());
        }
        if (maxDate.isPresent()) {
            queries.add(RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(maxDate.get())))._toQuery());
        } else {
            queries.add(RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery());
        }
        if (minScore.isPresent()) {
            queries.add(RangeQuery.of(p -> p.field("averageRating").gte(JsonData.of(minScore.get())))._toQuery());
        }

        return performQuery(queries, sort, 50);

    }

    /**
     * Gets documents from the index without performing any query, only shows movies previous to 2023
     *
     * @return List of movies ordered by descending startYear and starting in 2023
     */
    @Override
    public List<Movie> getDocuments() {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("startYear").order(SortOrder.Desc)).build();
        List<Query> queries = new LinkedList<>();
        Query beforeThisYear = RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery();
        queries.add(beforeThisYear);
        return performQuery(queries, sort, 50);
    }

    /**
     * Performs a match query over the parameter
     *
     * @param query, string with the title to be looked for
     * @return List of movies associated to the query
     */
    @Override
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

    @Override
    public List<Movie> getDocumentsGenre(String genre, int size) {
        SortOptions sort = new SortOptions.Builder().field(p -> p.field("averageRating").order(SortOrder.Desc)).build();
        CommonTermsQuery genreQuery = CommonTermsQuery.of(p -> p.field("genres").query(genre));
        List<Query> queries = new LinkedList<>();
        Query beforeThisYear = RangeQuery.of(p -> p.field("startYear").lte(JsonData.of(2023)))._toQuery();
        queries.add(genreQuery._toQuery());
        queries.add(beforeThisYear);

        return performQuery(queries, sort, size);
    }

    @Override
    public List<Movie> performQuery(List<Query> queries, SortOptions sort, int size) {
        List<Movie> movies = new LinkedList<>();
        //Query bulkQueries = BoolQuery.of(p->p.filter(queries).mustNot(MatchQuery.of(f->f.field("titleType").query(NOT_MOVIES))._toQuery()))._toQuery();
        queries.add(MatchQuery.of(p -> p.field("titleType").query("movie"))._toQuery());
        Query bulkQueries = BoolQuery.of(p -> p.must(queries))._toQuery();
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
