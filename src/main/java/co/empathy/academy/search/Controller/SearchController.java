package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import co.empathy.academy.search.Service.IndexService;
import co.empathy.academy.search.Service.IndexServiceImpl;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/movies")
public class SearchController {

    @Autowired
    ElasticLowClientImpl elasticLowClient;

    @GetMapping("/greet/{name}")
    public String greet(@PathVariable String name) {
        return "Hello " + name;
    }

    @GetMapping("/search/{query}")
    public JSONObject getJSON(@PathVariable String query) {
        JSONObject json = new JSONObject();

        //Variable to store clusterName
        String clusterName = "";

        //Handle the exceptions that may arise
        try{
            clusterName = this.elasticLowClient.search();
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
        IndexService indexService = new IndexServiceImpl(akas,basics,crew,episode,principals,ratings);
        try{
            indexService.indexAsync(akas, basics, crew, episode, principals, ratings);
        }
        catch (IOException e){
            return new ResponseEntity("Error in file", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("File accepted (QUEUED)", HttpStatus.OK);
    }
}
