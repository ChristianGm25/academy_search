package co.empathy.academy.search.controller;

import co.empathy.academy.search.Controller.IndexController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTest {

    private final IndexController indexcontroller = mock((IndexController.class));
    @Autowired
    MockMvc mvc;


}
