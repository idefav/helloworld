package com.idefav.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsDemoMessageAsJson() throws Exception {
        mockMvc.perform(get("/demo"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Hello, World!\"}", true));
    }

    @Test
    void preservesExistingSayRoute() throws Exception {
        mockMvc.perform(get("/demo/say/Codex"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello v2,Codex"));
    }
}
