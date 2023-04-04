package co.empathy.academy.search.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface IndexService {
    void read();

    CompletableFuture<String> indexAsync(long numMovies);

    void indexCreation();
    void indexDeletion();

    void setReaders(MultipartFile akas, MultipartFile basics, MultipartFile crew, MultipartFile episode, MultipartFile principals, MultipartFile ratings);
}
