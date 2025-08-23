package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.airplane.repository.SeatRepository;
import com.andesairlines.checkin_api.common.exception.BadRequestException;
import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.model.entity.Flight;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import com.andesairlines.checkin_api.passenger.model.dto.PassengerResponse;
import com.andesairlines.checkin_api.passenger.model.entity.Passenger;
import com.andesairlines.checkin_api.passenger.service.PassengerResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManualSeatAssignmentServiceTest {

    @Mock
    private BoardingPassRepository boardingPassRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PassengerResponseMapper passengerResponseMapper;

    @InjectMocks
    private ManualSeatAssignmentService manualSeatAssignmentService;

    private BoardingPass testBoardingPass;
    private Seat testSeat;
    private Flight testFlight;
    private Passenger testPassenger;
    private PassengerResponse testPassengerResponse;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setFlightId(1);
        testFlight.setAirplaneId(1);

        testPassenger = new Passenger();
        testPassenger.setPassengerId(1);
        testPassenger.setName("John Doe");

        testBoardingPass = new BoardingPass();
        testBoardingPass.setBoardingPassId(1);
        testBoardingPass.setSeatTypeId(1);
        testBoardingPass.setFlight(testFlight);
        testBoardingPass.setPassenger(testPassenger);

        testSeat = new Seat();
        testSeat.setSeatId(1);
        testSeat.setSeatRow(1);
        testSeat.setSeatColumn("A");
        testSeat.setSeatTypeId(1);
        testSeat.setAirplaneId(1);

        testPassengerResponse = new PassengerResponse();
        testPassengerResponse.setPassengerId(1);
        testPassengerResponse.setName("John Doe");
        testPassengerResponse.setSeatRow("1");
        testPassengerResponse.setSeatColumn("A");
    }

    @Test
    void assignSeat_Success() {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 1;
        String seatColumn = "A";

        when(boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId))
            .thenReturn(Optional.of(testBoardingPass));
        when(seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1))
            .thenReturn(Optional.of(testSeat));
        when(boardingPassRepository.findAssignedSeatsByFlightId(flightId))
            .thenReturn(Arrays.asList());
        when(boardingPassRepository.save(any(BoardingPass.class)))
            .thenReturn(testBoardingPass);
        when(passengerResponseMapper.mapToPassengerResponse(testBoardingPass, testSeat))
            .thenReturn(testPassengerResponse);

        // When
        PassengerResponse result = manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPassengerId());
        assertEquals("John Doe", result.getName());
        assertEquals("1", result.getSeatRow());
        assertEquals("A", result.getSeatColumn());

        verify(boardingPassRepository).findByFlightIdAndPassengerId(flightId, passengerId);
        verify(seatRepository).findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1);
        verify(boardingPassRepository).findAssignedSeatsByFlightId(flightId);
        verify(boardingPassRepository).save(testBoardingPass);
        verify(passengerResponseMapper).mapToPassengerResponse(testBoardingPass, testSeat);
        
        assertEquals(1, testBoardingPass.getSeatId());
    }

    @Test
    void assignSeat_BoardingPassNotFound() {
        // Given
        Integer flightId = 1;
        Integer passengerId = 999;
        Integer seatRow = 1;
        String seatColumn = "A";

        when(boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId))
            .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn));

        assertEquals("Boarding pass not found for passenger 999 on flight 1", exception.getMessage());
        verify(boardingPassRepository).findByFlightIdAndPassengerId(flightId, passengerId);
        verify(seatRepository, never()).findBySeatRowAndSeatColumnAndAirplaneId(anyInt(), anyString(), anyInt());
    }

    @Test
    void assignSeat_SeatNotFound() {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 99;
        String seatColumn = "Z";

        when(boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId))
            .thenReturn(Optional.of(testBoardingPass));
        when(seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1))
            .thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn));

        assertEquals("Seat not found: 99Z", exception.getMessage());
        verify(boardingPassRepository).findByFlightIdAndPassengerId(flightId, passengerId);
        verify(seatRepository).findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1);
        verify(boardingPassRepository, never()).findAssignedSeatsByFlightId(anyInt());
    }

    @Test
    void assignSeat_SeatTypeMismatch() {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 1;
        String seatColumn = "A";

        testSeat.setSeatTypeId(2); // Different seat type
        testBoardingPass.setSeatTypeId(1);

        when(boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId))
            .thenReturn(Optional.of(testBoardingPass));
        when(seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1))
            .thenReturn(Optional.of(testSeat));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn));

        assertEquals("Seat type mismatch. Expected: 1, Found: 2", exception.getMessage());
        verify(boardingPassRepository).findByFlightIdAndPassengerId(flightId, passengerId);
        verify(seatRepository).findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1);
        verify(boardingPassRepository, never()).findAssignedSeatsByFlightId(anyInt());
    }

    @Test
    void assignSeat_SeatAlreadyTaken() {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 1;
        String seatColumn = "A";

        BoardingPass occupiedSeat = new BoardingPass();
        occupiedSeat.setSeatId(1); // Same seat ID as testSeat

        when(boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId))
            .thenReturn(Optional.of(testBoardingPass));
        when(seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1))
            .thenReturn(Optional.of(testSeat));
        when(boardingPassRepository.findAssignedSeatsByFlightId(flightId))
            .thenReturn(Arrays.asList(occupiedSeat));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn));

        assertEquals("Seat 1A is already taken", exception.getMessage());
        verify(boardingPassRepository).findByFlightIdAndPassengerId(flightId, passengerId);
        verify(seatRepository).findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1);
        verify(boardingPassRepository).findAssignedSeatsByFlightId(flightId);
        verify(boardingPassRepository, never()).save(any(BoardingPass.class));
    }

    @Test
    void assignSeat_SeatAvailableAmongOtherAssignedSeats() {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 1;
        String seatColumn = "A";

        BoardingPass otherAssignedSeat = new BoardingPass();
        otherAssignedSeat.setSeatId(2); // Different seat ID

        when(boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId))
            .thenReturn(Optional.of(testBoardingPass));
        when(seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1))
            .thenReturn(Optional.of(testSeat));
        when(boardingPassRepository.findAssignedSeatsByFlightId(flightId))
            .thenReturn(Arrays.asList(otherAssignedSeat));
        when(boardingPassRepository.save(any(BoardingPass.class)))
            .thenReturn(testBoardingPass);
        when(passengerResponseMapper.mapToPassengerResponse(testBoardingPass, testSeat))
            .thenReturn(testPassengerResponse);

        // When
        PassengerResponse result = manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn);

        // Then
        assertNotNull(result);
        verify(boardingPassRepository).save(testBoardingPass);
        assertEquals(1, testBoardingPass.getSeatId());
    }

    @Test
    void assignSeat_NullSeatIdInAssignedSeats() {
        // Given
        Integer flightId = 1;
        Integer passengerId = 1;
        Integer seatRow = 1;
        String seatColumn = "A";

        BoardingPass boardingPassWithNullSeat = new BoardingPass();
        boardingPassWithNullSeat.setSeatId(null);

        when(boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId))
            .thenReturn(Optional.of(testBoardingPass));
        when(seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, 1))
            .thenReturn(Optional.of(testSeat));
        when(boardingPassRepository.findAssignedSeatsByFlightId(flightId))
            .thenReturn(Arrays.asList(boardingPassWithNullSeat));
        when(boardingPassRepository.save(any(BoardingPass.class)))
            .thenReturn(testBoardingPass);
        when(passengerResponseMapper.mapToPassengerResponse(testBoardingPass, testSeat))
            .thenReturn(testPassengerResponse);

        // When
        PassengerResponse result = manualSeatAssignmentService.assignSeat(flightId, passengerId, seatRow, seatColumn);

        // Then
        assertNotNull(result);
        verify(boardingPassRepository).save(testBoardingPass);
        assertEquals(1, testBoardingPass.getSeatId());
    }
}
