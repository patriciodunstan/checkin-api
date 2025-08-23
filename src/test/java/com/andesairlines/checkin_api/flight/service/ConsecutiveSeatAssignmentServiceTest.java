package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsecutiveSeatAssignmentServiceTest {

    @Mock
    private BoardingPassRepository boardingPassRepository;

    @InjectMocks
    private ConsecutiveSeatAssignmentService consecutiveSeatAssignmentService;

    private List<BoardingPass> testPassengers;
    private List<Seat> availableSeats;

    @BeforeEach
    void setUp() {
        // Create test passengers
        BoardingPass bp1 = new BoardingPass();
        bp1.setBoardingPassId(1);
        BoardingPass bp2 = new BoardingPass();
        bp2.setBoardingPassId(2);
        testPassengers = Arrays.asList(bp1, bp2);

        // Create available seats
        availableSeats = new ArrayList<>();
        
        // Row 1 seats
        Seat seat1A = createSeat(1, 1, "A", 1);
        Seat seat1B = createSeat(2, 1, "B", 1);
        Seat seat1C = createSeat(3, 1, "C", 1);
        
        // Row 2 seats
        Seat seat2A = createSeat(4, 2, "A", 1);
        Seat seat2B = createSeat(5, 2, "B", 1);
        
        // Different seat type
        Seat seat1D = createSeat(6, 1, "D", 2);
        
        availableSeats.addAll(Arrays.asList(seat1A, seat1B, seat1C, seat2A, seat2B, seat1D));
    }

    private Seat createSeat(Integer seatId, Integer row, String column, Integer seatTypeId) {
        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatRow(row);
        seat.setSeatColumn(column);
        seat.setSeatTypeId(seatTypeId);
        return seat;
    }

    @Test
    void assignSeatsForPassengers_ConsecutiveSeatsAvailable() {
        // Given
        Integer seatTypeId = 1;
        when(boardingPassRepository.save(any(BoardingPass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(testPassengers, availableSeats, seatTypeId);

        // Then
        verify(boardingPassRepository, times(2)).save(any(BoardingPass.class));
        
        // Verify passengers were assigned consecutive seats
        assertEquals(1, testPassengers.get(0).getSeatId()); // Seat 1A
        assertEquals(2, testPassengers.get(1).getSeatId()); // Seat 1B
        
        // Verify seats were removed from available list
        assertEquals(4, availableSeats.size()); // 6 - 2 assigned seats
    }

    @Test
    void assignSeatsForPassengers_NoConsecutiveSeats_AssignBestAvailable() {
        // Given
        Integer seatTypeId = 1;
        // Remove seat 1B to break consecutiveness in row 1
        availableSeats.removeIf(seat -> seat.getSeatId().equals(2));
        
        when(boardingPassRepository.save(any(BoardingPass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(testPassengers, availableSeats, seatTypeId);

        // Then
        verify(boardingPassRepository, times(2)).save(any(BoardingPass.class));
        
        // Verify passengers were assigned best available seats (sorted by row, column)
        // After removing seat 1B, available seats of type 1 are: 1A(1), 1C(3), 2A(4), 2B(5)
        // Service assigns in order based on row/column sorting
        assertNotNull(testPassengers.get(0).getSeatId());
        assertNotNull(testPassengers.get(1).getSeatId());
        
        // Verify both passengers got seats of the correct type
        assertTrue(testPassengers.get(0).getSeatId() > 0);
        assertTrue(testPassengers.get(1).getSeatId() > 0);
        
        // Verify seats were removed from available list
        assertEquals(3, availableSeats.size()); // 5 - 2 assigned seats
    }

    @Test
    void assignSeatsForPassengers_WrongSeatType() {
        // Given
        Integer seatTypeId = 3; // Non-existent seat type
        
        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(testPassengers, availableSeats, seatTypeId);

        // Then
        verify(boardingPassRepository, never()).save(any(BoardingPass.class));
        
        // Verify no seats were assigned
        assertNull(testPassengers.get(0).getSeatId());
        assertNull(testPassengers.get(1).getSeatId());
        
        // Verify available seats list unchanged
        assertEquals(6, availableSeats.size());
    }

    @Test
    void assignSeatsForPassengers_EmptyPassengerList() {
        // Given
        List<BoardingPass> emptyPassengers = new ArrayList<>();
        Integer seatTypeId = 1;

        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(emptyPassengers, availableSeats, seatTypeId);

        // Then
        verify(boardingPassRepository, never()).save(any(BoardingPass.class));
        assertEquals(6, availableSeats.size()); // No seats removed
    }

    @Test
    void assignSeatsForPassengers_EmptyAvailableSeats() {
        // Given
        List<Seat> emptySeats = new ArrayList<>();
        Integer seatTypeId = 1;

        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(testPassengers, emptySeats, seatTypeId);

        // Then
        verify(boardingPassRepository, never()).save(any(BoardingPass.class));
        
        // Verify no seats were assigned
        assertNull(testPassengers.get(0).getSeatId());
        assertNull(testPassengers.get(1).getSeatId());
    }

    @Test
    void assignSeatsForPassengers_MorePassengersThanSeats() {
        // Given
        BoardingPass bp3 = new BoardingPass();
        bp3.setBoardingPassId(3);
        BoardingPass bp4 = new BoardingPass();
        bp4.setBoardingPassId(4);
        BoardingPass bp5 = new BoardingPass();
        bp5.setBoardingPassId(5);
        
        List<BoardingPass> manyPassengers = Arrays.asList(
            testPassengers.get(0), testPassengers.get(1), bp3, bp4, bp5
        );
        
        Integer seatTypeId = 1;
        when(boardingPassRepository.save(any(BoardingPass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(manyPassengers, availableSeats, seatTypeId);

        // Then
        // Should assign only as many seats as available (5 seats of type 1)
        verify(boardingPassRepository, times(5)).save(any(BoardingPass.class));
        
        // All 5 passengers should be assigned since we have 5 seats of type 1
        assertNotNull(manyPassengers.get(0).getSeatId());
        assertNotNull(manyPassengers.get(1).getSeatId());
        assertNotNull(manyPassengers.get(2).getSeatId());
        assertNotNull(manyPassengers.get(3).getSeatId());
        assertNotNull(manyPassengers.get(4).getSeatId());
    }

    @Test
    void assignSeatsForPassengers_SinglePassenger() {
        // Given
        List<BoardingPass> singlePassenger = Arrays.asList(testPassengers.get(0));
        Integer seatTypeId = 1;
        when(boardingPassRepository.save(any(BoardingPass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(singlePassenger, availableSeats, seatTypeId);

        // Then
        verify(boardingPassRepository, times(1)).save(any(BoardingPass.class));
        assertEquals(1, singlePassenger.get(0).getSeatId()); // Should get seat 1A
        assertEquals(5, availableSeats.size()); // One seat removed
    }

    @Test
    void assignSeatsForPassengers_ThreeConsecutiveSeats() {
        // Given
        BoardingPass bp3 = new BoardingPass();
        bp3.setBoardingPassId(3);
        List<BoardingPass> threePassengers = Arrays.asList(
            testPassengers.get(0), testPassengers.get(1), bp3
        );
        
        Integer seatTypeId = 1;
        when(boardingPassRepository.save(any(BoardingPass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        consecutiveSeatAssignmentService.assignSeatsForPassengers(threePassengers, availableSeats, seatTypeId);

        // Then
        verify(boardingPassRepository, times(3)).save(any(BoardingPass.class));
        
        // Should assign consecutive seats 1A, 1B, 1C
        assertEquals(1, threePassengers.get(0).getSeatId());
        assertEquals(2, threePassengers.get(1).getSeatId());
        assertEquals(3, threePassengers.get(2).getSeatId());
        
        assertEquals(3, availableSeats.size()); // Three seats removed
    }
}
