package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.Movie;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IndexService {
    Map<String, Movie> read(int bulkSize);

    void indexAsync(MultipartFile akas, MultipartFile basics,
                    MultipartFile crew, MultipartFile episode, MultipartFile principals,
                    MultipartFile ratings) throws IOException;

}
