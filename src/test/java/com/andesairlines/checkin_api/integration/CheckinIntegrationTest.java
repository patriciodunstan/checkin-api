package com.andesairlines.checkin_api.integration;

import com.andesairlines.checkin_api.CheckinApiApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CheckinApiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CheckinIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void performCheckin_IntegrationTest() throws Exception {
        // Given - Test data loaded from test-data.sql
        Integer flightId = 1;

        // When & Then
        mockMvc.perform(get("/flights/{flightId}/passengers", flightId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.flightId").value(flightId))
                .andExpect(jsonPath("$.data.passengers").isArray());
    }

    @Test
    void assignSeat_IntegrationTest() throws Exception {
        // Given - Test data loaded from test-data.sql
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 1;
        String seatColumn = "A";

        // When & Then
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", flightId, passengerId)
                .param("seatRow", seatRow.toString())
                .param("seatColumn", seatColumn)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.passengerId").value(passengerId));
    }

    @Test
    void performCheckin_FlightNotFound_IntegrationTest() throws Exception {
        // Given
        Integer nonExistentFlightId = 999;

        // When & Then
        mockMvc.perform(get("/flights/{flightId}/passengers", nonExistentFlightId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
