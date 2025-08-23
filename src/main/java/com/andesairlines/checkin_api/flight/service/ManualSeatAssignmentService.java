package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.airplane.repository.SeatRepository;
import com.andesairlines.checkin_api.common.exception.BadRequestException;
import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import com.andesairlines.checkin_api.passenger.model.dto.PassengerResponse;
import com.andesairlines.checkin_api.passenger.service.PassengerResponseMapper;
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
public class ManualSeatAssignmentService {
    
    private final BoardingPassRepository boardingPassRepository;
    private final SeatRepository seatRepository;
    private final PassengerResponseMapper passengerResponseMapper;
    
    @CacheEvict(value = "flights", key = "#flightId")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public PassengerResponse assignSeat(Integer flightId, Integer passengerId, Integer seatRow, String seatColumn) {
        log.info("Assigning seat {}:{} to passenger {} on flight {}", seatRow, seatColumn, passengerId, flightId);
        
        // Buscar el boarding pass
        BoardingPass boardingPass = boardingPassRepository.findByFlightIdAndPassengerId(flightId, passengerId)
                .orElseThrow(() -> new NotFoundException("Boarding pass not found for passenger " + passengerId + " on flight " + flightId));
        
        // Buscar el asiento
        Seat seat = seatRepository.findBySeatRowAndSeatColumnAndAirplaneId(seatRow, seatColumn, boardingPass.getFlight().getAirplaneId())
                .orElseThrow(() -> new NotFoundException("Seat not found: " + seatRow + seatColumn));
        
        // Verificar que el tipo de asiento coincida
        if (!seat.getSeatTypeId().equals(boardingPass.getSeatTypeId())) {
            throw new BadRequestException("Seat type mismatch. Expected: " + boardingPass.getSeatTypeId() + ", Found: " + seat.getSeatTypeId());
        }
        
        // Verificar que el asiento esté disponible
        List<BoardingPass> assignedSeats = boardingPassRepository.findAssignedSeatsByFlightId(flightId);
        boolean seatTaken = assignedSeats.stream().anyMatch(bp -> seat.getSeatId().equals(bp.getSeatId()));
        
        if (seatTaken) {
            throw new BadRequestException("Seat " + seatRow + seatColumn + " is already taken");
        }
        
        // Asignar el asiento
        boardingPass.setSeatId(seat.getSeatId());
        boardingPassRepository.save(boardingPass);
        
        log.info("Successfully assigned seat {}:{} to passenger {} on flight {}", seatRow, seatColumn, passengerId, flightId);
        
        return passengerResponseMapper.mapToPassengerResponse(boardingPass, seat);
    }
}
