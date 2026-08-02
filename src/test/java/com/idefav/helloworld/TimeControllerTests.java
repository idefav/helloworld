package com.idefav.helloworld;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TimeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returnsCurrentUtcTimeInRfc3339Format() throws Exception {
        Instant before = Instant.now();
        MvcResult result = mockMvc.perform(get("/time"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        Instant after = Instant.now();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(body.isObject());
        assertEquals(1, body.size());

        JsonNode timeNode = body.get("time");
        assertNotNull(timeNode);
        assertTrue(timeNode.isTextual());

        String value = timeNode.textValue();
        assertFalse(value.isEmpty());
        assertTrue(value.matches(
                "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?Z"));

        Instant returned = Instant.parse(value);
        assertFalse(returned.isBefore(before.minusSeconds(1)));
        assertFalse(returned.isAfter(after.plusSeconds(1)));
    }
}
