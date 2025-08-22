package com.andesairlines.checkin_api.flight.service;


import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;
import com.andesairlines.checkin_api.flight.model.entity.Flight;
import com.andesairlines.checkin_api.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FlightService {

    private final FlightRepository flightRepository;

    @Cacheable(value = "flights", key = "#flightId")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public FlightResponse getFlightWithPassengers(Integer flightId) {
        log.info("Fetching flight with passengers for flightId: {}", flightId);

        Flight flight = flightRepository.findByIdWithDetails(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found with ID: " + flightId));

        return mapToFlightResponse(flight);
    }

    private FlightResponse mapToFlightResponse(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.setFlightId(flight.getFlightId());
        response.setTakeoffDateTime(flight.getTakeoffDateTime());
        response.setTakeoffAirport(flight.getTakeoffAirport());
        response.setLandingDateTime(flight.getLandingDateTime());
        response.setLandingAirport(flight.getLandingAirport());
        response.setAirplaneId(flight.getAirplaneId());

        if (flight.getBoardingPasses() != null) {
            response.setPassengers(
                    flight.getBoardingPasses().stream()
                            .map(bp -> {
                                FlightResponse.PassengerSeatInfo passengerInfo = new FlightResponse.PassengerSeatInfo();
                                if (bp.getPassenger() != null) {
                                    passengerInfo.setPassengerId(bp.getPassenger().getPassengerId());
                                    passengerInfo.setDni(bp.getPassenger().getDni());
                                    passengerInfo.setName(bp.getPassenger().getName());
                                    passengerInfo.setAge(bp.getPassenger().getAge());
                                    passengerInfo.setCountry(bp.getPassenger().getCountry());
                                }
                                passengerInfo.setSeatTypeId(bp.getSeatTypeId());
                                passengerInfo.setSeatId(bp.getSeatId());
                                if (bp.getSeat() != null) {
                                    passengerInfo.setSeatRow(bp.getSeat().getSeatRow().toString());
                                    passengerInfo.setSeatColumn(bp.getSeat().getSeatColumn());
                                }
                                return passengerInfo;
                            })
                            .toList()
            );
        }

        return response;
    }
}
