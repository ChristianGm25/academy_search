package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import co.empathy.academy.search.Model.Movie;
import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import co.empathy.academy.search.Repositories.QueryEngineImpl;
import co.empathy.academy.search.Service.IndexService;
import co.empathy.academy.search.Service.IndexServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class IndexController {

    @Autowired
    ElasticLowClientImpl elasticLowClient = new ElasticLowClientImpl(new ElasticSearchConfiguration());
    @Autowired
    IndexService indexService = new IndexServiceImpl(elasticLowClient);

    @Autowired
    QueryEngineImpl queriesEngine = new QueryEngineImpl(elasticLowClient);

    @Operation(summary = "Retrieve several movies without any filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List successfully retrieved"),
    })
    @GetMapping
    public ResponseEntity searchMatchAll() {
        List<Movie> movies = elasticLowClient.getDocuments();
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("hits", movies);
        returnJSON.put("facets", "");
        returnJSON.put("spellchecked", "");
        return new ResponseEntity(returnJSON, HttpStatus.OK);
    }

    @GetMapping("/{query}")
    public ResponseEntity searchQuery(@PathVariable String query) {
        List<Movie> movies = elasticLowClient.getDocumentsQuery(query);
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("hits", movies);
        returnJSON.put("facets", "");
        returnJSON.put("spellchecked", "");
        return new ResponseEntity(returnJSON, HttpStatus.OK);
    }

    @Operation(summary = "Index the files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files accepted and asynchronous call started"),
            @ApiResponse(responseCode = "406", description = "Some files have errors and are not accepted"),
    })
    @PostMapping
    public ResponseEntity indexAsync(@RequestBody MultipartFile akas, @RequestBody MultipartFile basics,
                                     @RequestBody MultipartFile crew, @RequestBody MultipartFile episodes,
                                     @RequestBody MultipartFile principals, @RequestBody MultipartFile ratings) {

        if ((basics == null) || (akas == null) || (crew == null) || (episodes == null) || (principals == null) ||
                (ratings == null)) {
            return new ResponseEntity("Error in file", HttpStatus.NOT_ACCEPTABLE);
        }
        this.indexService.setReaders(akas, basics, crew, episodes, principals, ratings);
        indexService.indexAsync(basics.getSize());
        return new ResponseEntity("File accepted (QUEUED)", HttpStatus.OK);
    }

    @Operation(summary = "Creates an index in elasticsearch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Index generated"),
            @ApiResponse(responseCode = "400", description = "Error creating an index"),
    })
    @PutMapping
    public ResponseEntity indexCreation() {
        try {
            indexService.indexCreation();
            return new ResponseEntity("Index generated", HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "Deletes the index if existing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Index deletion successfully done"),
            @ApiResponse(responseCode = "400", description = "Error deleting the index"),
    })
    @DeleteMapping
    public ResponseEntity indexDeletion() {
        try {
            indexService.indexDeletion();
            return new ResponseEntity("Index deleted", HttpStatus.ACCEPTED);
        } catch (IOException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
