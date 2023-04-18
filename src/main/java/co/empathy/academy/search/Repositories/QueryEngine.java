package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.empathy.academy.search.Model.Movie;

import java.util.List;
import java.util.Optional;

public interface QueryEngine {
    List<Movie> getDocumentsFiltered(Optional<String> genre, Optional<Integer> minDuration,
                                     Optional<Integer> maxDuration, Optional<String> minDate,
                                     Optional<String> maxDate, Optional<Integer> minScore);

    List<Movie> getDocuments();

    List<Movie> getDocumentsQuery(String query);

    List<Movie> getDocumentsGenre(String genre);

    List<Movie> performQuery(List<Query> queries, SortOptions sort, int size);


}
