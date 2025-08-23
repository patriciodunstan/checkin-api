package com.andesairlines.checkin_api.passenger.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.passenger.model.dto.PassengerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassengerResponseMapper {
    
    public PassengerResponse mapToPassengerResponse(BoardingPass boardingPass, Seat seat) {
        PassengerResponse response = new PassengerResponse();
        
        if (boardingPass.getPassenger() != null) {
            response.setPassengerId(boardingPass.getPassenger().getPassengerId());
            response.setDni(boardingPass.getPassenger().getDni());
            response.setName(boardingPass.getPassenger().getName());
            response.setAge(boardingPass.getPassenger().getAge());
            response.setCountry(boardingPass.getPassenger().getCountry());
        }
        
        response.setSeatTypeId(boardingPass.getSeatTypeId());
        response.setSeatId(seat.getSeatId());
        response.setSeatRow(String.valueOf(seat.getSeatRow()));
        response.setSeatColumn(seat.getSeatColumn());
        
        return response;
    }
}
