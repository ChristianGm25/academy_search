package co.empathy.academy.search.service;

import co.empathy.academy.search.Model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UsersTest {
    @Autowired
    MockMvc mvc;

    @Test
    void givenUser_whenInsert_thenInsert() throws Exception {
        User u = new User(1, "Carlos", "Carlos@gmail.com");

        mvc.perform(MockMvcRequestBuilders.post("/users/{id}", 1)
                        .content("{\"id\":" + u.getId() +
                                ",\"name\":\"" + u.getName() +
                                "\",\"email\":\"" + u.getEmail() +
                                "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("User successfully added\n"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    /*
    @Test
    void userNull_whenInsert_thenError() throws Exception{
        User u = null;

        mvc.perform(MockMvcRequestBuilders.post("/users/{id}", 1)
                        .content("{\"id\":" + u.getId() +
                                ",\"name\":\"" + u.getName() +
                                "\",\"email\":\"" + u.getEmail() +
                                "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("Bad Request\n"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
     */
}
