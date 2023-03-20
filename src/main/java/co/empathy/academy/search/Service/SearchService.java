package co.empathy.academy.search.Service;

import net.minidev.json.parser.ParseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SearchService {
    String search(String query) throws IOException, ParseException, InterruptedException;

    void indexAsync(MultipartFile akas, MultipartFile basics,
                    MultipartFile crew, MultipartFile episode, MultipartFile principals,
                    MultipartFile ratings);
}
