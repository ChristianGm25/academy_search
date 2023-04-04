package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.Movie;
import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IndexService {
    void read();

    void indexAsync(long numMovies);

    void indexCreation();
    void indexDeletion();

    void setReaders(MultipartFile akas, MultipartFile basics, MultipartFile crew, MultipartFile episode, MultipartFile principals, MultipartFile ratings);
}
