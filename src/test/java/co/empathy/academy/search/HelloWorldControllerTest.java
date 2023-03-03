package co.empathy.academy.search;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.rmi.server.ExportException;

@SpringBootTest
@AutoConfigureMockMvc
class HelloWorldControllerTest {

	@Autowired
	private MockMvc mvc;
	@Test
	void givenName_whenGreet_thenGreetingWithName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greet/{name}", "mariano"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Hello mariano"));
	}



}
