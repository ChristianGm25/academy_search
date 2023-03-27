package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Service.ElasticEngineImpl;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/movies")
public class SearchController {

    ElasticEngineImpl elasticEngine = new ElasticEngineImpl();
    @GetMapping("/greet/{name}")
    public String greet(@PathVariable String name) {
        return "Hello " + name;
    }

    @GetMapping("/search/{query}")
    public JSONObject getJSON(@PathVariable String query) {
        JSONObject json = new JSONObject();
        //String uri = "http://localhost:9200";
        //RestTemplate rt = new RestTemplate();
        //JSONObject result = rt.getForObject(uri, JSONObject.class);


        //Variable to store clusterName
        String clusterName = "";

        //Handle the exceptions that may arise
        try{
            clusterName = this.elasticEngine.search();
        } catch (Exception e) {
            json.appendField("query", "Error");
            json.appendField("clusterName", "Error");
        }

        json.appendField("query", query);
        json.appendField("clusterName", clusterName);
        return json;
    }

    @PostMapping
    public ResponseEntity indexAsync(@RequestBody MultipartFile akas, @RequestBody MultipartFile basics,
                                     @RequestBody MultipartFile crew, @RequestBody MultipartFile episode,
                                     @RequestBody MultipartFile principals, @RequestBody MultipartFile ratings) {
        this.elasticEngine.indexAsync(akas, basics, crew, episode, principals, ratings);
        return new ResponseEntity("File accepted (QUEUED)", HttpStatus.OK);
    }
}
