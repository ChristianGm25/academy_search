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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class IndexController {

    @Autowired
    ElasticLowClientImpl elasticLowClient = new ElasticLowClientImpl(new ElasticSearchConfiguration());
    @Autowired
    IndexService indexService = new IndexServiceImpl(elasticLowClient);

    @Autowired
    QueryEngineImpl queriesEngine = new QueryEngineImpl(new ElasticSearchConfiguration().elasticsearchClient());

    @Operation(summary = "Retrieve several movies without any filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List successfully retrieved"),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchMatchAll() {
        List<Movie> movies = queriesEngine.getDocuments();
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("hits", movies);
        returnJSON.put("facets", "");
        returnJSON.put("spellchecked", "");
        return new ResponseEntity(returnJSON, HttpStatus.OK);
    }

    @Operation(summary = "Retrieves docs associated to that query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results retrieved"),
    })
    @GetMapping(value = "/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchQuery(@PathVariable String query) {
        List<Movie> movies = queriesEngine.getDocumentsQuery(query);
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("hits", movies);
        returnJSON.put("facets", "");
        returnJSON.put("spellchecked", "");
        return new ResponseEntity(returnJSON, HttpStatus.OK);
    }

    @Operation(summary = "Retrieves docs associated with a set of filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results retrieved"),
    })
    @GetMapping(value = "/filters", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Movie>> searchQueryFilters(@RequestParam(required = false) Optional<String> genre,
                                                          @RequestParam(required = false) Optional<Integer> minDuration,
                                                          @RequestParam(required = false) Optional<Integer> maxDuration,
                                                          @RequestParam(required = false) Optional<String> minDate,
                                                          @RequestParam(required = false) Optional<String> maxDate,
                                                          @RequestParam(required = false) Optional<Integer> minScore) {
        List<Movie> movies = queriesEngine.getDocumentsFiltered(genre, minDuration, maxDuration, minDate, maxDate, minScore);
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("hits", movies);
        returnJSON.put("facets", "");
        returnJSON.put("spellchecked", "");
        return new ResponseEntity(returnJSON, HttpStatus.OK);
    }

    @Operation(summary = "Retrieves docs associated to that genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results retrieved"),
    })
    @GetMapping("/genre/{genre}")
    public ResponseEntity searchQueryGenre(@PathVariable String genre) {
        List<Movie> movies = queriesEngine.getDocumentsGenre(genre);
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
