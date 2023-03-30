package co.empathy.academy.search;

import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import co.empathy.academy.search.Service.SearchService;
import co.empathy.academy.search.Service.SearchServiceImpl;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class SearchApplicationTests {

	@Autowired
	private MockMvc mvc;


    @Test
	void givenQuery() throws Exception {

        JSONObject json = new JSONObject();
		json.appendField("query", "hola");
		json.appendField("clusterName", "docker-cluster");
		mvc.perform(MockMvcRequestBuilders.get("/search/{query}", "hola"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(json.toJSONString()));
	}


    @Test
	void givenQuery_whenSearch_thenReturnResult() throws IOException, ParseException, InterruptedException {
		String exampleQuery = "example query";
		String expectedResult = "result for example query";

		ElasticLowClientImpl client = mock(ElasticLowClientImpl.class);

		given(client.search()).willReturn(expectedResult);

		SearchService searchService = new SearchServiceImpl(client);

		String result = searchService.search(exampleQuery);

		assertEquals(expectedResult, result);
		verify(client).search();
	}

	@Test
	void givenQuery_whenErrorDuringSearch_thenLetItPropagate() throws IOException, ParseException, InterruptedException {
		String exampleQuery = "example query";
		ElasticLowClientImpl client = mock(ElasticLowClientImpl.class);
		given(client.search()).willThrow(RuntimeException.class);

		SearchService searchService = new SearchServiceImpl(client);

		// In this case we just check that the exception bubbles up
		assertThrows(RuntimeException.class, () -> searchService.search(exampleQuery));
	}

	@Test
	void givenBlankQuery_whenSearch_thenDoNotExecuteQueryAndReturnEmptyString() throws IOException, ParseException, InterruptedException {
		ElasticLowClientImpl client = mock(ElasticLowClientImpl.class);
		SearchService searchService = new SearchServiceImpl(client);
		String result = searchService.search("   ");

		assertTrue(result == "");
		verifyNoInteractions(client);
	}
}
