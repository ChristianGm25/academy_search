package co.empathy.academy.search.service;

import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
class IndexIMDBTest {
    @Autowired
    private MockMvc mvc;

    private final ElasticLowClientImpl elasticLowClient = mock(ElasticLowClientImpl.class);


}
