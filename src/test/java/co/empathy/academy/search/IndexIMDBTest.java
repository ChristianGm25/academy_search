package co.empathy.academy.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class IndexIMDBTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void compareStrings() {
        assertTrue("tt9916856".compareTo("tt9916857") < 0);
    }

}
