package com.andesairlines.checkin_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckinIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void performCheckin_IntegrationTest() throws Exception {
        // Given - Mock flight ID
        Integer flightId = 999; // Non-existent flight

        // When & Then - Should return 404
        mockMvc.perform(get("/flights/{flightId}/passengers", flightId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignSeat_IntegrationTest() throws Exception {
        // Given - Non-existent flight and passenger
        Integer flightId = 999;
        Integer passengerId = 999;
        Integer seatRow = 1;
        String seatColumn = "A";

        // When & Then - Should return 404
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", flightId, passengerId)
                .param("seatRow", seatRow.toString())
                .param("seatColumn", seatColumn)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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
