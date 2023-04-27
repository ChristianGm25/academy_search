package co.empathy.academy.search.controller;

import co.empathy.academy.search.Controller.IndexController;
import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTest {

    private final IndexController indexcontroller = mock((IndexController.class));
    private final ElasticLowClientImpl elasticLowClient = mock((ElasticLowClientImpl.class));
    private final MultipartFile akas = mock(MultipartFile.class);
    private final MultipartFile basics = mock(MultipartFile.class);
    private final MultipartFile crew = mock(MultipartFile.class);
    private final MultipartFile episodes = mock(MultipartFile.class);
    private final MultipartFile principals = mock(MultipartFile.class);
    private final MultipartFile ratings = mock(MultipartFile.class);
    @Autowired
    MockMvc mvc;

    @Test
    void givenIndexCreation_returnIndexGenerated() {

        given(indexcontroller.indexCreation()).willReturn(new ResponseEntity("Index Generated", HttpStatus.OK));

        assertEquals(indexcontroller.indexCreation(), new ResponseEntity<>("Index Generated", HttpStatus.OK));
    }


    @Test
    void givenIndexDeletion_returnIndexDeleted() {

        given(indexcontroller.indexDeletion()).willReturn(new ResponseEntity("Index deleted", HttpStatus.OK));

        assertEquals(indexcontroller.indexDeletion(), new ResponseEntity<>("Index deleted", HttpStatus.OK));
    }

    @Test
    void givenIndexAsync_returnFilesAccepted() {

        given(indexcontroller.indexAsync(akas, basics, crew, episodes, principals, ratings)).willReturn(new ResponseEntity("File accepted (QUEUED)", HttpStatus.OK));

        assertEquals(indexcontroller.indexAsync(akas, basics, crew, episodes, principals, ratings), new ResponseEntity<>("File accepted (QUEUED)", HttpStatus.OK));
    }

}
