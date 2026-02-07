package dk.datamuseum.mobilereg.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test that the list of possible item classes is correct when editing
     * an item that is in a location and has a child container.
     */
    @Test
    @WithMockUser(username = "reg", authorities = {"CHANGE_ITEMS", "DELETE_ITEMS"})
    void editPalle461() throws Exception {
        this.mockMvc.perform(get("/items/edit")
            .param("id","11001745"))
            .andExpectAll(
                status().isOk(),
                content().string(containsString("<span>Palle</span>")),
                content().string(containsString("<span>Rum</span>")),
                content().string(not(containsString("<span>Lokation</span>")))
            );
    }

}
