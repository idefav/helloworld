package com.idefav.helloworld;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

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

    private static final DateTimeFormatter STRICT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss", Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returnsCurrentUtcTimeInConfirmedFormat() throws Exception {
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
        assertEquals(19, value.length());
        assertTrue(value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        assertFalse(value.contains("T"));
        assertFalse(value.contains("Z"));
        assertFalse(value.contains("."));
        assertFalse(value.matches(".*[+-]\\d{2}:?\\d{2}$"));

        LocalDateTime parsed = LocalDateTime.parse(value, STRICT_TIME_FORMATTER);
        Instant returned = parsed.toInstant(ZoneOffset.UTC);
        assertFalse(returned.isBefore(before.minusSeconds(1)));
        assertFalse(returned.isAfter(after.plusSeconds(1)));
    }
}
