package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.model.entity.Flight;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightResponseMapper {
    
    public FlightResponse mapToFlightResponse(Flight flight, List<BoardingPass> boardingPasses) {
        FlightResponse response = new FlightResponse();
        response.setFlightId(flight.getFlightId());
        response.setTakeoffDateTime(flight.getTakeoffDateTime());
        response.setTakeoffAirport(flight.getTakeoffAirport());
        response.setLandingDateTime(flight.getLandingDateTime());
        response.setLandingAirport(flight.getLandingAirport());
        response.setAirplaneId(flight.getAirplaneId());
        
        if (boardingPasses != null) {
            response.setPassengers(
                boardingPasses.stream()
                    .map(this::mapToPassengerSeatInfo)
                    .toList()
            );
        }
        
        return response;
    }
    
    private FlightResponse.PassengerSeatInfo mapToPassengerSeatInfo(BoardingPass bp) {
        FlightResponse.PassengerSeatInfo passengerInfo = new FlightResponse.PassengerSeatInfo();
        
        if (bp.getPassenger() != null) {
            passengerInfo.setPassengerId(bp.getPassenger().getPassengerId());
            passengerInfo.setDni(bp.getPassenger().getDni());
            passengerInfo.setName(bp.getPassenger().getName());
            passengerInfo.setAge(bp.getPassenger().getAge());
            passengerInfo.setCountry(bp.getPassenger().getCountry());
        }
        
        passengerInfo.setBoardingPassId(bp.getBoardingPassId());
        passengerInfo.setPurchaseId(bp.getPurchaseId());
        passengerInfo.setSeatTypeId(bp.getSeatTypeId());
        passengerInfo.setSeatId(bp.getSeatId());
        
        if (bp.getSeat() != null) {
            passengerInfo.setSeatRow(bp.getSeat().getSeatRow().toString());
            passengerInfo.setSeatColumn(bp.getSeat().getSeatColumn());
        }
        
        return passengerInfo;
    }
}
