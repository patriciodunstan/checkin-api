package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.model.entity.Flight;
import com.andesairlines.checkin_api.passenger.model.entity.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FlightResponseMapperTest {

    @InjectMocks
    private FlightResponseMapper flightResponseMapper;

    private Flight testFlight;
    private List<BoardingPass> testBoardingPasses;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setFlightId(1);
        testFlight.setTakeoffDateTime(121213123);
        testFlight.setTakeoffAirport("SCL");
        testFlight.setLandingDateTime(1223112);
        testFlight.setLandingAirport("LIM");
        testFlight.setAirplaneId(101);

        // Create test boarding passes
        BoardingPass bp1 = createBoardingPass(1, 1, 100, 1, 1);
        Passenger passenger1 = createPassenger(1, "12345678", "John Doe", 30, "Chile");
        bp1.setPassenger(passenger1);
        Seat seat1 = createSeat(1, 1, "A");
        bp1.setSeat(seat1);

        BoardingPass bp2 = createBoardingPass(2, 2, 200, 2, 2);
        Passenger passenger2 = createPassenger(2, "87654321", "Jane Smith", 25, "Peru");
        bp2.setPassenger(passenger2);
        Seat seat2 = createSeat(2, 1, "B");
        bp2.setSeat(seat2);

        testBoardingPasses = Arrays.asList(bp1, bp2);
    }

    private BoardingPass createBoardingPass(Integer id, Integer purchaseId, Integer seatTypeId, Integer seatId, Integer boardingPassId) {
        BoardingPass bp = new BoardingPass();
        bp.setBoardingPassId(boardingPassId);
        bp.setPurchaseId(purchaseId);
        bp.setSeatTypeId(seatTypeId);
        bp.setSeatId(seatId);
        return bp;
    }

    private Passenger createPassenger(Integer id, String dni, String name, Integer age, String country) {
        Passenger passenger = new Passenger();
        passenger.setPassengerId(id);
        passenger.setDni(dni);
        passenger.setName(name);
        passenger.setAge(age);
        passenger.setCountry(country);
        return passenger;
    }

    private Seat createSeat(Integer seatId, Integer row, String column) {
        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatRow(row);
        seat.setSeatColumn(column);
        return seat;
    }

    @Test
    void mapToFlightResponse_Success() {
        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(testFlight, testBoardingPasses);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getFlightId());
        assertEquals(121213123, result.getTakeoffDateTime());
        assertEquals("SCL", result.getTakeoffAirport());
        assertEquals(1223112, result.getLandingDateTime());
        assertEquals("LIM", result.getLandingAirport());
        assertEquals(101, result.getAirplaneId());

        assertNotNull(result.getPassengers());
        assertEquals(2, result.getPassengers().size());

        // Verify first passenger
        FlightResponse.PassengerSeatInfo passenger1 = result.getPassengers().get(0);
        assertEquals(1, passenger1.getPassengerId());
        assertEquals("12345678", passenger1.getDni());
        assertEquals("John Doe", passenger1.getName());
        assertEquals(30, passenger1.getAge());
        assertEquals("Chile", passenger1.getCountry());
        assertEquals(1, passenger1.getBoardingPassId());
        assertEquals(1, passenger1.getPurchaseId());
        assertEquals(100, passenger1.getSeatTypeId());
        assertEquals(1, passenger1.getSeatId());
        assertEquals("1", passenger1.getSeatRow());
        assertEquals("A", passenger1.getSeatColumn());

        // Verify second passenger
        FlightResponse.PassengerSeatInfo passenger2 = result.getPassengers().get(1);
        assertEquals(2, passenger2.getPassengerId());
        assertEquals("87654321", passenger2.getDni());
        assertEquals("Jane Smith", passenger2.getName());
        assertEquals(25, passenger2.getAge());
        assertEquals("Peru", passenger2.getCountry());
        assertEquals(2, passenger2.getBoardingPassId());
        assertEquals(2, passenger2.getPurchaseId());
        assertEquals(200, passenger2.getSeatTypeId());
        assertEquals(2, passenger2.getSeatId());
        assertEquals("1", passenger2.getSeatRow());
        assertEquals("B", passenger2.getSeatColumn());
    }

    @Test
    void mapToFlightResponse_NullBoardingPasses() {
        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(testFlight, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getFlightId());
        assertEquals("SCL", result.getTakeoffAirport());
        assertEquals("LIM", result.getLandingAirport());
        assertEquals(101, result.getAirplaneId());
        assertNull(result.getPassengers());
    }

    @Test
    void mapToFlightResponse_EmptyBoardingPasses() {
        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(testFlight, Arrays.asList());

        // Then
        assertNotNull(result);
        assertEquals(1, result.getFlightId());
        assertNotNull(result.getPassengers());
        assertTrue(result.getPassengers().isEmpty());
    }

    @Test
    void mapToFlightResponse_BoardingPassWithNullPassenger() {
        // Given
        BoardingPass bpWithNullPassenger = createBoardingPass(3, 3, 300, 3, 3);
        bpWithNullPassenger.setPassenger(null);
        Seat seat3 = createSeat(3, 2, "A");
        bpWithNullPassenger.setSeat(seat3);

        List<BoardingPass> boardingPassesWithNull = Arrays.asList(bpWithNullPassenger);

        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(testFlight, boardingPassesWithNull);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPassengers().size());

        FlightResponse.PassengerSeatInfo passengerInfo = result.getPassengers().get(0);
        assertNull(passengerInfo.getPassengerId());
        assertNull(passengerInfo.getDni());
        assertNull(passengerInfo.getName());
        assertNull(passengerInfo.getAge());
        assertNull(passengerInfo.getCountry());
        assertEquals(3, passengerInfo.getBoardingPassId());
        assertEquals(3, passengerInfo.getPurchaseId());
        assertEquals(300, passengerInfo.getSeatTypeId());
        assertEquals(3, passengerInfo.getSeatId());
        assertEquals("2", passengerInfo.getSeatRow());
        assertEquals("A", passengerInfo.getSeatColumn());
    }

    @Test
    void mapToFlightResponse_BoardingPassWithNullSeat() {
        // Given
        BoardingPass bpWithNullSeat = createBoardingPass(4, 4, 400, 4, 4);
        Passenger passenger4 = createPassenger(4, "11111111", "Test User", 35, "Colombia");
        bpWithNullSeat.setPassenger(passenger4);
        bpWithNullSeat.setSeat(null);

        List<BoardingPass> boardingPassesWithNullSeat = Arrays.asList(bpWithNullSeat);

        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(testFlight, boardingPassesWithNullSeat);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPassengers().size());

        FlightResponse.PassengerSeatInfo passengerInfo = result.getPassengers().get(0);
        assertEquals(4, passengerInfo.getPassengerId());
        assertEquals("11111111", passengerInfo.getDni());
        assertEquals("Test User", passengerInfo.getName());
        assertEquals(35, passengerInfo.getAge());
        assertEquals("Colombia", passengerInfo.getCountry());
        assertEquals(4, passengerInfo.getBoardingPassId());
        assertEquals(4, passengerInfo.getPurchaseId());
        assertEquals(400, passengerInfo.getSeatTypeId());
        assertEquals(4, passengerInfo.getSeatId());
        assertNull(passengerInfo.getSeatRow());
        assertNull(passengerInfo.getSeatColumn());
    }

    @Test
    void mapToFlightResponse_BoardingPassWithNullPassengerAndSeat() {
        // Given
        BoardingPass bpWithNulls = createBoardingPass(5, 5, 500, 5, 5);
        bpWithNulls.setPassenger(null);
        bpWithNulls.setSeat(null);

        List<BoardingPass> boardingPassesWithNulls = Arrays.asList(bpWithNulls);

        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(testFlight, boardingPassesWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPassengers().size());

        FlightResponse.PassengerSeatInfo passengerInfo = result.getPassengers().get(0);
        assertNull(passengerInfo.getPassengerId());
        assertNull(passengerInfo.getDni());
        assertNull(passengerInfo.getName());
        assertNull(passengerInfo.getAge());
        assertNull(passengerInfo.getCountry());
        assertEquals(5, passengerInfo.getBoardingPassId());
        assertEquals(5, passengerInfo.getPurchaseId());
        assertEquals(500, passengerInfo.getSeatTypeId());
        assertEquals(5, passengerInfo.getSeatId());
        assertNull(passengerInfo.getSeatRow());
        assertNull(passengerInfo.getSeatColumn());
    }

    @Test
    void mapToFlightResponse_FlightWithNullValues() {
        // Given
        Flight flightWithNulls = new Flight();
        flightWithNulls.setFlightId(null);
        flightWithNulls.setTakeoffDateTime(null);
        flightWithNulls.setTakeoffAirport(null);
        flightWithNulls.setLandingDateTime(null);
        flightWithNulls.setLandingAirport(null);
        flightWithNulls.setAirplaneId(null);

        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(flightWithNulls, testBoardingPasses);

        // Then
        assertNotNull(result);
        assertNull(result.getFlightId());
        assertNull(result.getTakeoffDateTime());
        assertNull(result.getTakeoffAirport());
        assertNull(result.getLandingDateTime());
        assertNull(result.getLandingAirport());
        assertNull(result.getAirplaneId());
        assertNotNull(result.getPassengers());
        assertEquals(2, result.getPassengers().size());
    }

    @Test
    void mapToFlightResponse_MixedBoardingPasses() {
        // Given - Mix of normal, null passenger, and null seat boarding passes
        BoardingPass normalBp = testBoardingPasses.get(0);
        
        BoardingPass nullPassengerBp = createBoardingPass(6, 6, 600, 6, 6);
        nullPassengerBp.setPassenger(null);
        Seat seat6 = createSeat(6, 3, "C");
        nullPassengerBp.setSeat(seat6);
        
        BoardingPass nullSeatBp = createBoardingPass(7, 7, 700, 7, 7);
        Passenger passenger7 = createPassenger(7, "22222222", "Mixed User", 40, "Brazil");
        nullSeatBp.setPassenger(passenger7);
        nullSeatBp.setSeat(null);

        List<BoardingPass> mixedBoardingPasses = Arrays.asList(normalBp, nullPassengerBp, nullSeatBp);

        // When
        FlightResponse result = flightResponseMapper.mapToFlightResponse(testFlight, mixedBoardingPasses);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getPassengers().size());

        // Normal boarding pass
        FlightResponse.PassengerSeatInfo normal = result.getPassengers().get(0);
        assertNotNull(normal.getPassengerId());
        assertNotNull(normal.getName());
        assertNotNull(normal.getSeatRow());

        // Null passenger boarding pass
        FlightResponse.PassengerSeatInfo nullPassenger = result.getPassengers().get(1);
        assertNull(nullPassenger.getPassengerId());
        assertNull(nullPassenger.getName());
        assertNotNull(nullPassenger.getSeatRow());

        // Null seat boarding pass
        FlightResponse.PassengerSeatInfo nullSeat = result.getPassengers().get(2);
        assertNotNull(nullSeat.getPassengerId());
        assertNotNull(nullSeat.getName());
        assertNull(nullSeat.getSeatRow());
    }
}
