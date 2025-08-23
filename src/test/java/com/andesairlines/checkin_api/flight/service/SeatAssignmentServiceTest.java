package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.airplane.repository.SeatRepository;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatAssignmentServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BoardingPassRepository boardingPassRepository;

    @Mock
    private GroupSeatAssignmentService groupSeatAssignmentService;

    @InjectMocks
    private SeatAssignmentService seatAssignmentService;

    private List<Seat> testSeats;
    private Map<Integer, List<BoardingPass>> testGroups;

    @BeforeEach
    void setUp() {
        // Create test seats
        testSeats = new ArrayList<>();
        for (int row = 1; row <= 10; row++) {
            for (char col = 'A'; col <= 'F'; col++) {
                Seat seat = new Seat();
                seat.setSeatId(row * 10 + (col - 'A'));
                seat.setSeatRow(row);
                seat.setSeatColumn(String.valueOf(col));
                seat.setAirplaneId(100);
                testSeats.add(seat);
            }
        }

        // Create test groups
        testGroups = new HashMap<>();
        
        // Group 1: 2 passengers
        BoardingPass bp1 = new BoardingPass();
        bp1.setBoardingPassId(1);
        bp1.setPurchaseId(1);
        bp1.setSeatId(null);

        BoardingPass bp2 = new BoardingPass();
        bp2.setBoardingPassId(2);
        bp2.setPurchaseId(1);
        bp2.setSeatId(null);

        testGroups.put(1, Arrays.asList(bp1, bp2));

        // Group 2: 1 passenger
        BoardingPass bp3 = new BoardingPass();
        bp3.setBoardingPassId(3);
        bp3.setPurchaseId(2);
        bp3.setSeatId(null);

        testGroups.put(2, Arrays.asList(bp3));
    }

    @Test
    void assignSeatsForAllGroups_Success() {
        // Given
        Integer airplaneId = 100;
        when(seatRepository.findByAirplaneId(airplaneId)).thenReturn(testSeats);

        // When
        seatAssignmentService.assignSeatsForAllGroups(testGroups, airplaneId);

        // Then
        verify(seatRepository).findByAirplaneId(airplaneId);
        verify(groupSeatAssignmentService, times(2)).assignSeatsForGroup(any(), any(), eq(airplaneId));
        
        // Verify each group is processed
        verify(groupSeatAssignmentService).assignSeatsForGroup(
            eq(testGroups.get(1)), any(), eq(airplaneId));
        verify(groupSeatAssignmentService).assignSeatsForGroup(
            eq(testGroups.get(2)), any(), eq(airplaneId));
    }

    @Test
    void assignSeatsForAllGroups_WithAlreadyAssignedSeats() {
        // Given
        Integer airplaneId = 100;
        
        // Create boarding passes with some already assigned seats
        BoardingPass bp1 = new BoardingPass();
        bp1.setBoardingPassId(1);
        bp1.setPurchaseId(1);
        bp1.setSeatId(10); // Already assigned

        BoardingPass bp2 = new BoardingPass();
        bp2.setBoardingPassId(2);
        bp2.setPurchaseId(1);
        bp2.setSeatId(null);

        Map<Integer, List<BoardingPass>> groupsWithAssigned = new HashMap<>();
        groupsWithAssigned.put(1, Arrays.asList(bp1, bp2));

        when(seatRepository.findByAirplaneId(airplaneId)).thenReturn(testSeats);

        // When
        seatAssignmentService.assignSeatsForAllGroups(groupsWithAssigned, airplaneId);

        // Then
        verify(seatRepository).findByAirplaneId(airplaneId);
        verify(groupSeatAssignmentService).assignSeatsForGroup(
            any(), argThat(availableSeats -> {
                // Verify that seat with ID 10 is filtered out from available seats
                return availableSeats.stream().noneMatch(seat -> seat.getSeatId().equals(10));
            }), eq(airplaneId));
    }

    @Test
    void assignSeatsForAllGroups_EmptyGroups() {
        // Given
        Integer airplaneId = 100;
        Map<Integer, List<BoardingPass>> emptyGroups = new HashMap<>();
        when(seatRepository.findByAirplaneId(airplaneId)).thenReturn(testSeats);

        // When
        seatAssignmentService.assignSeatsForAllGroups(emptyGroups, airplaneId);

        // Then
        verify(seatRepository).findByAirplaneId(airplaneId);
        verifyNoInteractions(groupSeatAssignmentService);
    }

    @Test
    void assignSeatsForAllGroups_NoAvailableSeats() {
        // Given
        Integer airplaneId = 100;
        when(seatRepository.findByAirplaneId(airplaneId)).thenReturn(Arrays.asList());

        // When
        seatAssignmentService.assignSeatsForAllGroups(testGroups, airplaneId);

        // Then
        verify(seatRepository).findByAirplaneId(airplaneId);
        verify(groupSeatAssignmentService, times(2)).assignSeatsForGroup(
            any(), eq(Arrays.asList()), eq(airplaneId));
    }

    @Test
    void assignSeatsForAllGroups_AllSeatsAlreadyAssigned() {
        // Given
        Integer airplaneId = 100;
        
        // Create boarding passes where all seats are already assigned
        BoardingPass bp1 = new BoardingPass();
        bp1.setBoardingPassId(1);
        bp1.setPurchaseId(1);
        bp1.setSeatId(10);

        BoardingPass bp2 = new BoardingPass();
        bp2.setBoardingPassId(2);
        bp2.setPurchaseId(1);
        bp2.setSeatId(11);

        Map<Integer, List<BoardingPass>> fullyAssignedGroups = new HashMap<>();
        fullyAssignedGroups.put(1, Arrays.asList(bp1, bp2));

        // Create seats that match the assigned seat IDs
        List<Seat> limitedSeats = Arrays.asList(
            createSeat(10, 1, "A"),
            createSeat(11, 1, "B")
        );

        when(seatRepository.findByAirplaneId(airplaneId)).thenReturn(limitedSeats);

        // When
        seatAssignmentService.assignSeatsForAllGroups(fullyAssignedGroups, airplaneId);

        // Then
        verify(seatRepository).findByAirplaneId(airplaneId);
        verify(groupSeatAssignmentService).assignSeatsForGroup(
            any(), eq(Arrays.asList()), eq(airplaneId));
    }

    private Seat createSeat(Integer seatId, Integer row, String column) {
        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatRow(row);
        seat.setSeatColumn(column);
        seat.setAirplaneId(100);
        return seat;
    }
}
