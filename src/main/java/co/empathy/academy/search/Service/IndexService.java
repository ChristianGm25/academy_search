package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.Movie;

import java.util.Map;

public interface IndexService {
    Map<String, Movie> read(int bulkSize);
}
