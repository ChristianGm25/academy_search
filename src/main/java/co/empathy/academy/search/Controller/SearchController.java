package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Service.ElasticEngineImpl;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @GetMapping("/greet/{name}")
    public String greet(@PathVariable String name){
        return "Hello " + name;
    }

    @GetMapping("/search/{query}")
    public JSONObject getJSON(@PathVariable String query){
        JSONObject json = new JSONObject();
        //String uri = "http://localhost:9200";
        //RestTemplate rt = new RestTemplate();
        //JSONObject result = rt.getForObject(uri, JSONObject.class);

        //Create the elastic client
        ElasticEngineImpl c = new ElasticEngineImpl();
        //Variable to store clusterName
        String clusterName = "";

        //Handle the exceptions that may arise
        try{
            clusterName = c.search();
        }
        catch (Exception e){
            json.appendField("query", "Error");
            json.appendField("clusterName", "Error");
        }

        json.appendField("query", query);
        json.appendField("clusterName", clusterName);
        return json;
    }
}
