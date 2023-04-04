package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Configuration.ElasticSearchConfiguration;
import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import co.empathy.academy.search.Service.IndexService;
import co.empathy.academy.search.Service.IndexServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequestMapping("/movies")
public class IndexController {

    @Autowired
    ElasticLowClientImpl elasticLowClient = new ElasticLowClientImpl(new ElasticSearchConfiguration());
    @Autowired
    IndexService indexService = new IndexServiceImpl(elasticLowClient);
    @PostMapping
    public ResponseEntity indexAsync(@RequestBody MultipartFile akas, @RequestBody MultipartFile basics,
                                     @RequestBody MultipartFile crew, @RequestBody MultipartFile episode,
                                     @RequestBody MultipartFile principals, @RequestBody MultipartFile ratings) {
        if ((basics == null) || (akas == null) || (crew == null) || (episode == null) || (principals == null) ||
                (ratings == null)){
            return new ResponseEntity("Error in file", HttpStatus.BAD_REQUEST);
        }
        this.indexService.setReaders(akas,basics,crew,episode,principals,ratings);
        indexService.indexAsync(basics.getSize());
        return new ResponseEntity("File accepted (QUEUED)", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity indexCreation(){
        indexService.indexCreation();
        return new ResponseEntity("Index generated", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity indexDeletion(){
        indexService.indexDeletion();
        return new ResponseEntity("Index deleted", HttpStatus.OK);
    }
}
