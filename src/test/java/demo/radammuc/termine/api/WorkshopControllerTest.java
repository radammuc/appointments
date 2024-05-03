package demo.radammuc.termine.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkshopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTermin() throws Exception {
        mockMvc.perform(get("/werkstatt/1/termin/1")
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getTermine_emptyDataArray() throws Exception {
        mockMvc.perform(get("/werkstatt/1/termine?von=2024-01-01T08:00Z&bis=2024-01-01T18:00Z&leistungId=FIX")
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(content().string(equalTo("[]")));
    }
}