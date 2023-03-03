package co.empathy.academy.search.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElasticEngineImpl implements ElasticEngine {

    @Override
    public String search() throws IOException, ParseException {

        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        Request request = new Request("GET", "/");
        String response = EntityUtils.toString(restClient.performRequest(request).getEntity());

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);

        return json.getAsString("cluster_name");
    }
}