package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import com.andesairlines.checkin_api.passenger.model.entity.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupSeatAssignmentServiceTest {

    @Mock
    private BoardingPassRepository boardingPassRepository;

    @Mock
    private ConsecutiveSeatAssignmentService consecutiveSeatAssignmentService;

    @InjectMocks
    private GroupSeatAssignmentService groupSeatAssignmentService;

    private List<BoardingPass> testGroup;
    private List<Seat> availableSeats;

    @BeforeEach
    void setUp() {
        availableSeats = new ArrayList<>();
        
        // Create available seats
        Seat seat1 = createSeat(1, 1, "A", 1);
        Seat seat2 = createSeat(2, 1, "B", 1);
        Seat seat3 = createSeat(3, 1, "C", 2);
        availableSeats.addAll(Arrays.asList(seat1, seat2, seat3));
    }

    private Seat createSeat(Integer seatId, Integer row, String column, Integer seatTypeId) {
        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatRow(row);
        seat.setSeatColumn(column);
        seat.setSeatTypeId(seatTypeId);
        return seat;
    }

    private BoardingPass createBoardingPass(Integer id, Integer seatTypeId, Integer age, String name) {
        BoardingPass bp = new BoardingPass();
        bp.setBoardingPassId(id);
        bp.setSeatTypeId(seatTypeId);
        
        Passenger passenger = new Passenger();
        passenger.setPassengerId(id);
        passenger.setAge(age);
        passenger.setName(name);
        bp.setPassenger(passenger);
        
        return bp;
    }

    @Test
    void assignSeatsForGroup_AdultsOnly() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 25, "Adult1"),
            createBoardingPass(2, 1, 30, "Adult2")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
    }

    @Test
    void assignSeatsForGroup_MinorsOnly() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 10, "Minor1"),
            createBoardingPass(2, 1, 15, "Minor2")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
    }

    @Test
    void assignSeatsForGroup_MinorsWithAdults() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 30, "Adult1"),
            createBoardingPass(2, 1, 10, "Minor1"),
            createBoardingPass(3, 1, 25, "Adult2")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
    }

    @Test
    void assignSeatsForGroup_MixedSeatTypes() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 30, "Adult1"),
            createBoardingPass(2, 2, 25, "Adult2"),
            createBoardingPass(3, 1, 35, "Adult3")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        // Should be called twice: once for seat type 1, once for seat type 2
        verify(consecutiveSeatAssignmentService, times(2))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), anyInt());
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(2));
    }

    @Test
    void assignSeatsForGroup_MinorsWithAdultsMixedSeatTypes() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 30, "Adult1"),
            createBoardingPass(2, 2, 10, "Minor1"),
            createBoardingPass(3, 1, 15, "Minor2"),
            createBoardingPass(4, 2, 25, "Adult2")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        // Should be called twice: once for seat type 1, once for seat type 2
        verify(consecutiveSeatAssignmentService, times(2))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), anyInt());
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(2));
    }

    @Test
    void assignSeatsForGroup_EmptyGroup() {
        // Given
        testGroup = new ArrayList<>();
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, never())
            .assignSeatsForPassengers(anyList(), anyList(), anyInt());
    }

    @Test
    void assignSeatsForGroup_SingleAdult() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 30, "Adult1")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
    }

    @Test
    void assignSeatsForGroup_SingleMinor() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 10, "Minor1")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
    }

    @Test
    void assignSeatsForGroup_EdgeCaseAge18() {
        // Given - Age 18 should be considered adult
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 18, "Adult18"),
            createBoardingPass(2, 1, 17, "Minor17")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
    }

    @Test
    void assignSeatsForGroup_LargeGroup() {
        // Given
        testGroup = Arrays.asList(
            createBoardingPass(1, 1, 30, "Adult1"),
            createBoardingPass(2, 1, 25, "Adult2"),
            createBoardingPass(3, 1, 10, "Minor1"),
            createBoardingPass(4, 1, 12, "Minor2"),
            createBoardingPass(5, 2, 35, "Adult3"),
            createBoardingPass(6, 2, 8, "Minor3")
        );
        Integer airplaneId = 1;

        // When
        groupSeatAssignmentService.assignSeatsForGroup(testGroup, availableSeats, airplaneId);

        // Then
        verify(consecutiveSeatAssignmentService, times(2))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), anyInt());
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(1));
        verify(consecutiveSeatAssignmentService, times(1))
            .assignSeatsForPassengers(anyList(), eq(availableSeats), eq(2));
    }
}
