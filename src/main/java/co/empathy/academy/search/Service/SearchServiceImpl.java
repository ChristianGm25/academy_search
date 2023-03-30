package co.empathy.academy.search.Service;

import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import net.minidev.json.parser.ParseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class SearchServiceImpl implements SearchService{

    private final ElasticLowClientImpl elasticLowClient;

    public SearchServiceImpl(ElasticLowClientImpl elasticLowClient) {
        this.elasticLowClient = elasticLowClient;
    }


    @Override
    public String search(String query) throws IOException, ParseException, InterruptedException {
        return elasticLowClient.search();
    }
}
