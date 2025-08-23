package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.model.entity.Flight;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import com.andesairlines.checkin_api.flight.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckinServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private BoardingPassRepository boardingPassRepository;

    @Mock
    private SeatAssignmentService seatAssignmentService;

    @Mock
    private FlightResponseMapper flightResponseMapper;

    @InjectMocks
    private CheckinService checkinService;

    private Flight testFlight;
    private List<BoardingPass> testBoardingPasses;
    private FlightResponse testFlightResponse;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setFlightId(1);
        testFlight.setAirplaneId(100);
        testFlight.setTakeoffDateTime(1234567890);
        testFlight.setLandingDateTime(1234567890);
        testFlight.setTakeoffAirport("SCL");
        testFlight.setLandingAirport("LIM");

        BoardingPass bp1 = new BoardingPass();
        bp1.setBoardingPassId(1);
        bp1.setPurchaseId(1);
        bp1.setSeatId(null);

        BoardingPass bp2 = new BoardingPass();
        bp2.setBoardingPassId(2);
        bp2.setPurchaseId(1);
        bp2.setSeatId(null);

        testBoardingPasses = Arrays.asList(bp1, bp2);

        testFlightResponse = new FlightResponse();
        testFlightResponse.setFlightId(1);
        testFlightResponse.setAirplaneId(100);
    }

    @Test
    void performCheckin_Success() {
        // Given
        Integer flightId = 1;
        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightIdOrderedByPurchase(flightId))
                .thenReturn(testBoardingPasses);
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId))
                .thenReturn(testBoardingPasses);
        when(flightResponseMapper.mapToFlightResponse(eq(testFlight), any()))
                .thenReturn(testFlightResponse);

        // When
        FlightResponse result = checkinService.performCheckin(flightId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getFlightId());
        assertEquals(100, result.getAirplaneId());

        verify(flightRepository).findByIdWithAirplane(flightId);
        verify(boardingPassRepository).findBoardingPassesByFlightIdOrderedByPurchase(flightId);
        verify(seatAssignmentService).assignSeatsForAllGroups(any(Map.class), eq(100));
        verify(boardingPassRepository).findBoardingPassesByFlightId(flightId);
        verify(flightResponseMapper).mapToFlightResponse(eq(testFlight), eq(testBoardingPasses));
    }

    @Test
    void performCheckin_FlightNotFound() {
        // Given
        Integer flightId = 999;
        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> checkinService.performCheckin(flightId));
        
        assertEquals("Flight not found with ID: 999", exception.getMessage());
        
        verify(flightRepository).findByIdWithAirplane(flightId);
        verifyNoInteractions(boardingPassRepository, seatAssignmentService, flightResponseMapper);
    }

    @Test
    void performCheckin_EmptyBoardingPasses() {
        // Given
        Integer flightId = 1;
        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightIdOrderedByPurchase(flightId))
                .thenReturn(List.of());
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId))
                .thenReturn(List.of());
        when(flightResponseMapper.mapToFlightResponse(eq(testFlight), any()))
                .thenReturn(testFlightResponse);

        // When
        FlightResponse result = checkinService.performCheckin(flightId);

        // Then
        assertNotNull(result);
        verify(seatAssignmentService).assignSeatsForAllGroups(any(Map.class), eq(100));
    }

    @Test
    void performCheckin_GroupsCorrectlyFormed() {
        // Given
        Integer flightId = 1;
        BoardingPass bp1 = new BoardingPass();
        bp1.setBoardingPassId(1);
        bp1.setPurchaseId(1);

        BoardingPass bp2 = new BoardingPass();
        bp2.setBoardingPassId(2);
        bp2.setPurchaseId(1);

        BoardingPass bp3 = new BoardingPass();
        bp3.setBoardingPassId(3);
        bp3.setPurchaseId(2);

        List<BoardingPass> boardingPasses = Arrays.asList(bp1, bp2, bp3);

        when(flightRepository.findByIdWithAirplane(flightId)).thenReturn(Optional.of(testFlight));
        when(boardingPassRepository.findBoardingPassesByFlightIdOrderedByPurchase(flightId))
                .thenReturn(boardingPasses);
        when(boardingPassRepository.findBoardingPassesByFlightId(flightId))
                .thenReturn(boardingPasses);
        when(flightResponseMapper.mapToFlightResponse(eq(testFlight), any()))
                .thenReturn(testFlightResponse);

        // When
        checkinService.performCheckin(flightId);

        // Then
        verify(seatAssignmentService).assignSeatsForAllGroups(argThat(groups -> {
            return groups.size() == 2 && 
                   groups.get(1).size() == 2 && 
                   groups.get(2).size() == 1;
        }), eq(100));
    }
}
