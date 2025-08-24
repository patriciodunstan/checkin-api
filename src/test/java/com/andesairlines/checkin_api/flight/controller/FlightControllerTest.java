package com.andesairlines.checkin_api.flight.controller;

import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;
import com.andesairlines.checkin_api.flight.service.CheckinService;
import com.andesairlines.checkin_api.flight.service.ManualSeatAssignmentService;
import com.andesairlines.checkin_api.passenger.model.dto.PassengerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
@ActiveProfiles("test")
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckinService checkinService;

    @MockBean
    private ManualSeatAssignmentService manualSeatAssignmentService;

    private FlightResponse testFlightResponse;
    private PassengerResponse testPassengerResponse;

    @BeforeEach
    void setUp() {
        testFlightResponse = new FlightResponse();
        testFlightResponse.setFlightId(1);
        testFlightResponse.setAirplaneId(100);
        testFlightResponse.setTakeoffDateTime(1234567890);
        testFlightResponse.setLandingDateTime(1234567890);
        testFlightResponse.setTakeoffAirport("SCL");
        testFlightResponse.setLandingAirport("LIM");

        testPassengerResponse = new PassengerResponse();
        testPassengerResponse.setPassengerId(1);
        testPassengerResponse.setName("John Doe");
        testPassengerResponse.setDni("12345678");
        testPassengerResponse.setAge(30);
        testPassengerResponse.setCountry("Chile");
    }

    @Test
    void getFlightWithPassengers_Success() throws Exception {
        // Given
        Integer flightId = 1;
        when(checkinService.performCheckin(flightId)).thenReturn(testFlightResponse);

        // When & Then
        mockMvc.perform(get("/flights/{flightId}/passengers", flightId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.flightId").value(1))
                .andExpect(jsonPath("$.data.airplaneId").value(100))
                .andExpect(jsonPath("$.data.takeoffAirport").value("SCL"))
                .andExpect(jsonPath("$.data.landingAirport").value("LIM"));

        verify(checkinService).performCheckin(flightId);
    }

    @Test
    void getFlightWithPassengers_FlightNotFound() throws Exception {
        // Given
        Integer flightId = 999;
        when(checkinService.performCheckin(flightId))
                .thenThrow(new NotFoundException("Flight not found with ID: " + flightId));

        // When & Then
        mockMvc.perform(get("/flights/{flightId}/passengers", flightId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(checkinService).performCheckin(flightId);
    }

    @Test
    void assignSeat_Success() throws Exception {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 10;
        String seatColumn = "A";

        when(manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn))
                .thenReturn(testPassengerResponse);

        // When & Then
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", flightId, passengerId)
                .param("seatRow", seatRow.toString())
                .param("seatColumn", seatColumn)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.passengerId").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.dni").value("12345678"));

        verify(manualSeatAssignmentService).assignSeat(flightId, passengerId, seatRow, seatColumn);
    }

    @Test
    void assignSeat_InvalidSeatColumn() throws Exception {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 10;
        String invalidSeatColumn = "Z";

        // When & Then
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", flightId, passengerId)
                .param("seatRow", seatRow.toString())
                .param("seatColumn", invalidSeatColumn)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manualSeatAssignmentService);
    }

    @Test
    void assignSeat_MissingParameters() throws Exception {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;

        // When & Then - Missing seatRow parameter
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", flightId, passengerId)
                .param("seatColumn", "A")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        // When & Then - Missing seatColumn parameter
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", flightId, passengerId)
                .param("seatRow", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(manualSeatAssignmentService);
    }

    @Test
    void assignSeat_PassengerNotFound() throws Exception {
        // Given
        Integer flightId = 1;
        Integer passengerId = 999;
        Integer seatRow = 10;
        String seatColumn = "A";

        when(manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn))
                .thenThrow(new NotFoundException("Passenger not found with ID: " + passengerId));

        // When & Then
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", flightId, passengerId)
                .param("seatRow", seatRow.toString())
                .param("seatColumn", seatColumn)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(manualSeatAssignmentService).assignSeat(flightId, passengerId, seatRow, seatColumn);
    }

    @Test
    void assignSeat_NullPathVariables() throws Exception {
        // Given - Mock service to throw NotFoundException for non-existent IDs
        when(manualSeatAssignmentService.assignSeat(999, 999, 10, "A"))
                .thenThrow(new NotFoundException("Flight or passenger not found"));

        // When & Then
        mockMvc.perform(put("/flights/{flightId}/passengers/{passengerId}/seat", "999", "999")
                .param("seatRow", "10")
                .param("seatColumn", "A")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(manualSeatAssignmentService).assignSeat(999, 999, 10, "A");
    }
}