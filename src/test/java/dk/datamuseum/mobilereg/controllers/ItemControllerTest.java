package dk.datamuseum.mobilereg.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
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

    /**
     * Test moving an item by inputting the id of the new container.
     * If the move happened then the new parent is Tapeten and
     * there is a move activity.
     */
    @Test
    @WithMockUser(username = "reg", authorities = {"CHANGE_ITEMS", "VIEW_ITEMS"})
    void moveWithparentid() throws Exception {
        mockMvc.perform(post("/items/updateplace/{itemid}", 11002571)
            .with(csrf())
            .param("placementid", "10000031"))
            .andExpectAll(
                status().isFound()
            );
        mockMvc.perform(get("/items/view/{id}", 11002571))
            .andExpectAll(
                status().isOk(),
                content().string(containsString("<a href=\"/items/view/10000031\">Tapeten</a>")),
                content().string(containsString("Fra <a href=\"10000032\">Sandby magasin</a> til <a href=\"10000031\">Tapeten</a>.")),
                content().string(not(containsString("<a href=\"/items/view/10000034\">Charlotteskolen</a>")))
            );
    }

    /**
     * Test moving an item by scanning the QR code of the new container.
     * If the move happened then the new parent is Flyttekasse PCSW03 and
     * there is a move activity.
     */
    @Test
    @WithMockUser(username = "reg", authorities = {"CHANGE_ITEMS", "VIEW_ITEMS"})
    void moveWithQRcode() throws Exception {
        mockMvc.perform(post("/items/qrupdateplace/{itemid}", 10000001)
            .with(csrf())
            .param("placementid","50003333"))
            .andExpectAll(
                status().isFound()
            );
        mockMvc.perform(get("/items/view/{id}", 10000001))
            .andExpectAll(
                status().isOk(),
                content().string(containsString("<a href=\"/items/view/11002191\">Flyttekasse PCSW03</a>")),
                content().string(containsString("Fra <a href=\"10000034\">Charlotteskolen</a> til <a href=\"11002191\">Flyttekasse PCSW03</a>.")),
                content().string(not(containsString("<a href=\"/items/view/10000034\">Charlotteskolen</a>")))
            );
    }

}
