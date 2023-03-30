package co.empathy.academy.search.Repositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ElasticLowClientImpl implements ElasticLowClient {

    private final ElasticSearchConfiguration elasticSearchConfiguration;

    public ElasticLowClientImpl(ElasticSearchConfiguration elasticSearchConfiguration){
        this.elasticSearchConfiguration = elasticSearchConfiguration;
    }

    @Override
    public String search() throws IOException {
        return elasticSearchConfiguration.elasticsearchClient().cluster().health().clusterName();
    }


}