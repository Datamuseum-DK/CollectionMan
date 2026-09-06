package dk.datamuseum.mobilereg.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
@AutoConfigureMockMvc
class WebApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * It is possible to get the frontpage for anonymous users.
     */
    @Test
    void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/"))
            .andExpectAll(
                status().isOk(),
                content().string(containsString("<a href=\"about\">Brugervejledning</a>"))
            );
    }

    /**
     * Any authenticated user can see the user profile.
     */
    @Test
    @WithMockUser(username = "reg")
    void userProfileAuth() throws Exception {
        this.mockMvc.perform(get("/userprofile")).andExpect(status().isOk())
            .andExpect(content().string(containsString("<h3>Autoriteter</h3>")));
    }

}
