package co.empathy.academy.search.Repositories;

import co.empathy.academy.search.Model.Movie;

import java.io.IOException;
import java.util.List;


public interface ElasticLowClient {

    String search();

    List<Movie> getDocuments();

    void indexCreation() throws IOException;

    void indexDeletion() throws IOException;

    void indexMovies(List<Movie> movies) throws IOException;

}
