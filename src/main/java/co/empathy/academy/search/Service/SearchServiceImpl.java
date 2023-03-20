package co.empathy.academy.search.Service;

import net.minidev.json.parser.ParseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class SearchServiceImpl implements SearchService{

    private final ElasticEngineImpl elasticClient;

    public SearchServiceImpl(ElasticEngineImpl elasticClient) {
        this.elasticClient = elasticClient;
    }

    @Override
    public String search(String query) throws IOException, ParseException, InterruptedException {
        if (!query.isBlank()) {
            return elasticClient.search();
        }
        return "";
    }

    @Override
    public void indexAsync(MultipartFile akas, MultipartFile basics,
                           MultipartFile crew, MultipartFile episode, MultipartFile principals,
                           MultipartFile ratings) {
        if (akas == null) {
            //throw error
        }
        elasticClient.indexAsync(akas, basics, crew, episode, principals, ratings);
    }
}
