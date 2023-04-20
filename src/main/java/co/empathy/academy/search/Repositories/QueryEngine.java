package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.empathy.academy.search.Model.Movie;

import java.util.List;
import java.util.Optional;

public interface QueryEngine {
    List<Movie> getDocumentsFiltered(Optional<String> query, Optional<String> genre, Optional<Integer> minDuration,
                                     Optional<Integer> maxDuration, Optional<Integer> minDate,
                                     Optional<Integer> maxDate, Optional<Double> minScore);

    List<Movie> getDocuments();

    List<Movie> getDocumentsGenre(String genre, int size);

    List<Movie> performQuery(List<Query> queries, SortOptions sort, int size, boolean filterNumVotes);

    List<Movie> getRecommendedMovies(List<Movie> selectedMovies);


}
