package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.model.entity.Flight;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import com.andesairlines.checkin_api.flight.repository.FlightRepository;
import com.andesairlines.checkin_api.passenger.model.entity.Passenger;
import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private BoardingPassRepository boardingPassRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;
    private List<BoardingPass> testBoardingPasses;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setFlightId(1);
        testFlight.setTakeoffDateTime(123123123);
        testFlight.setTakeoffAirport("SCL");
        testFlight.setLandingDateTime(12312312);
        testFlight.setLandingAirport("LIM");
        testFlight.setAirplaneId(1);

        // Create test boarding passes
        BoardingPass bp1 = new BoardingPass();
        bp1.setBoardingPassId(1);
        bp1.setSeatTypeId(1);
        bp1.setSeatId(1);
        
        Passenger passenger1 = new Passenger();
        passenger1.setPassengerId(1);
        passenger1.setDni("12345678");
        passenger1.setName("John Doe");
        passenger1.setAge(30);
        passenger1.setCountry("Chile");
        bp1.setPassenger(passenger1);
        
        Seat seat1 = new Seat();
        seat1.setSeatId(1);
        seat1.setSeatRow(1);
        seat1.setSeatColumn("A");
        bp1.setSeat(seat1);

        BoardingPass bp2 = new BoardingPass();
        bp2.setBoardingPassId(2);
        bp2.setSeatTypeId(2);
        bp2.setSeatId(2);
        
        Passenger passenger2 = new Passenger();
        passenger2.setPassengerId(2);
        passenger2.setDni("87654321");
        passenger2.setName("Jane Smith");
        passenger2.setAge(25);
        passenger2.setCountry("Peru");
        bp2.setPassenger(passenger2);
        
        Seat seat2 = new Seat();
        seat2.setSeatId(2);
        seat2.setSeatRow(1);
        seat2.setSeatColumn("B");
        bp2.setSeat(seat2);

        testBoardingPasses = Arrays.asList(bp1, bp2);
    }

    @Test
    void getFlightWithPassengers_Success() {
        // Given
        Integer flightId = 1;
        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId)).thenReturn(testBoardingPasses);

        // When
        FlightResponse result = flightService.getFlighWithPassengers(flightId);

        // Then
        assertNotNull(result);
        assertEquals(flightId, result.getFlightId());
        assertEquals("SCL", result.getTakeoffAirport());
        assertEquals("LIM", result.getLandingAirport());
        assertEquals(1, result.getAirplaneId());
        assertEquals(2, result.getPassengers().size());
        
        FlightResponse.PassengerSeatInfo passenger1 = result.getPassengers().get(0);
        assertEquals(1, passenger1.getPassengerId());
        assertEquals("12345678", passenger1.getDni());
        assertEquals("John Doe", passenger1.getName());
        assertEquals(30, passenger1.getAge());
        assertEquals("Chile", passenger1.getCountry());
        assertEquals("1", passenger1.getSeatRow());
        assertEquals("A", passenger1.getSeatColumn());

        verify(flightRepository).findByIdWithAirplane(flightId);
        verify(boardingPassRepository).findBoardingPassesByFlightId(flightId);
    }

    @Test
    void getFlightWithPassengers_FlightNotFound() {
        // Given
        Integer flightId = 999;
        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> flightService.getFlighWithPassengers(flightId));
        
        assertEquals("Flight not found with id: 999", exception.getMessage());
        verify(flightRepository).findByIdWithAirplane(flightId);
        verify(boardingPassRepository, never()).findBoardingPassesByFlightId(any());
    }

    @Test
    void getFlightWithPassengers_EmptyBoardingPasses() {
        // Given
        Integer flightId = 1;
        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId)).thenReturn(Arrays.asList());

        // When
        FlightResponse result = flightService.getFlighWithPassengers(flightId);

        // Then
        assertNotNull(result);
        assertEquals(flightId, result.getFlightId());
        assertTrue(result.getPassengers().isEmpty());
        
        verify(flightRepository).findByIdWithAirplane(flightId);
        verify(boardingPassRepository).findBoardingPassesByFlightId(flightId);
    }

    @Test
    void getFlightWithPassengers_NullBoardingPasses() {
        // Given
        Integer flightId = 1;
        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId)).thenReturn(null);

        // When
        FlightResponse result = flightService.getFlighWithPassengers(flightId);

        // Then
        assertNotNull(result);
        assertEquals(flightId, result.getFlightId());
        assertNull(result.getPassengers());
        
        verify(flightRepository).findByIdWithAirplane(flightId);
        verify(boardingPassRepository).findBoardingPassesByFlightId(flightId);
    }

    @Test
    void getFlightWithPassengers_BoardingPassWithNullPassenger() {
        // Given
        Integer flightId = 1;
        BoardingPass bpWithNullPassenger = new BoardingPass();
        bpWithNullPassenger.setBoardingPassId(3);
        bpWithNullPassenger.setSeatTypeId(1);
        bpWithNullPassenger.setSeatId(3);
        bpWithNullPassenger.setPassenger(null);
        
        Seat seat3 = new Seat();
        seat3.setSeatId(3);
        seat3.setSeatRow(2);
        seat3.setSeatColumn("A");
        bpWithNullPassenger.setSeat(seat3);

        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId)).thenReturn(Arrays.asList(bpWithNullPassenger));

        // When
        FlightResponse result = flightService.getFlighWithPassengers(flightId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPassengers().size());
        FlightResponse.PassengerSeatInfo passengerInfo = result.getPassengers().get(0);
        assertNull(passengerInfo.getPassengerId());
        assertNull(passengerInfo.getDni());
        assertNull(passengerInfo.getName());
        assertNull(passengerInfo.getAge());
        assertNull(passengerInfo.getCountry());
        assertEquals("2", passengerInfo.getSeatRow());
        assertEquals("A", passengerInfo.getSeatColumn());
    }

    @Test
    void getFlightWithPassengers_BoardingPassWithNullSeat() {
        // Given
        Integer flightId = 1;
        BoardingPass bpWithNullSeat = new BoardingPass();
        bpWithNullSeat.setBoardingPassId(3);
        bpWithNullSeat.setSeatTypeId(1);
        bpWithNullSeat.setSeatId(3);
        bpWithNullSeat.setSeat(null);
        
        Passenger passenger3 = new Passenger();
        passenger3.setPassengerId(3);
        passenger3.setDni("11111111");
        passenger3.setName("Test User");
        passenger3.setAge(35);
        passenger3.setCountry("Colombia");
        bpWithNullSeat.setPassenger(passenger3);

        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId)).thenReturn(Arrays.asList(bpWithNullSeat));

        // When
        FlightResponse result = flightService.getFlighWithPassengers(flightId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPassengers().size());
        FlightResponse.PassengerSeatInfo passengerInfo = result.getPassengers().get(0);
        assertEquals(3, passengerInfo.getPassengerId());
        assertEquals("Test User", passengerInfo.getName());
        assertNull(passengerInfo.getSeatRow());
        assertNull(passengerInfo.getSeatColumn());
    }
}
