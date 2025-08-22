package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.common.exception.BadRequestException;
import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.airplane.repository.SeatRepository;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import com.andesairlines.checkin_api.passenger.model.dto.PassengerResponse;
import com.andesairlines.checkin_api.passenger.model.entity.Passenger;
import com.andesairlines.checkin_api.passenger.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatAssignmentService {

    private final BoardingPassRepository boardingPassRepository;
    private final SeatRepository seatRepository;

    @CacheEvict(value = "flights", key = "#flightId")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public PassengerResponse assignSeat(Integer flightId, Integer passengerId, Integer seatRow, String seatColumn){
        log.info("Assigning seat {}:{} to passenger {} on flight {}", seatRow, seatColumn, passengerId, flightId);

        BoardingPass boardingPass = boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId)
                .orElseThrow(() -> new NotFoundException("Boarding pass not found for passenger" + passengerId + "on flight" + flightId));

        Seat seat = seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, boardingPass.getFlight().getAirplaneId())
                .orElseThrow(() -> new NotFoundException("Seat not found:" + seatRow + seatColumn));

        if(!seat.getSeatTypeId().equals(boardingPass.getSeatTypeId())){
            throw new BadRequestException("Seat type mismatch. Expected: " + boardingPass.getSeatTypeId() + ", Found: " + seat.getSeatTypeId());
        }

        List<BoardingPass> assignedSeats = boardingPassRepository.findAssignedSeatsByFlightId(flightId);
        boolean seatTaken = assignedSeats.stream().anyMatch(bp -> seat.getSeatId().equals(bp.getSeatId()));

        if(seatTaken){
            throw new BadRequestException("seat" + seatRow + seatColumn + "is already taken");
        }

        boardingPass.setSeatId(seat.getSeatId());
        boardingPassRepository.save(boardingPass);

        log.info("Successfully assigned seat {}:{} to passenger {} on flight {}", seatRow, seatColumn, passengerId, flightId);

        return mapToPassengerResponse(boardingPass, seat);
    }

    private PassengerResponse mapToPassengerResponse(BoardingPass boardingPass, Seat seat){
        PassengerResponse response = new PassengerResponse();
        if(boardingPass.getPassenger() != null){
            response.setPassengerId(boardingPass.getPassenger().getPassengerId());
            response.setDni(boardingPass.getPassenger().getDni());
            response.setName(boardingPass.getPassenger().getName());
            response.setAge(boardingPass.getPassenger().getAge());
            response.setCountry(boardingPass.getPassenger().getCountry());
        }
        response.setSeatTypeId(boardingPass.getSeatTypeId());
        response.setSeatId(seat.getSeatId());
        response.setSeatRow(seat.getSeatRow());
        response.setSeatColumn(seat.getSeatColumn());
    }
}
