package co.empathy.academy.search.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface IndexService {
    void read();

    CompletableFuture<String> indexAsync(long numMovies);

    void indexCreation() throws IOException;

    void indexDeletion() throws IOException;

    void setReaders(MultipartFile akas, MultipartFile basics, MultipartFile crew, MultipartFile episode, MultipartFile principals, MultipartFile ratings);
}
