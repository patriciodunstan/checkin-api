package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.airplane.repository.SeatRepository;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatAssignmentService {
    
    private final SeatRepository seatRepository;
    private final BoardingPassRepository boardingPassRepository;
    private final GroupSeatAssignmentService groupSeatAssignmentService;
    
    public void assignSeatsForAllGroups(Map<Integer, List<BoardingPass>> groups, Integer airplaneId) {
        log.info("Starting seat assignment for {} groups", groups.size());
        
        // Obtener todos los asientos del avión
        List<Seat> allSeats = seatRepository.findByAirplaneId(airplaneId);
        
        // Obtener todos los boarding passes para filtrar asientos ya asignados
        List<BoardingPass> allBoardingPasses = groups.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        
        // Filtrar asientos ya asignados
        Set<Integer> assignedSeatIds = allBoardingPasses.stream()
                .filter(bp -> bp.getSeatId() != null)
                .map(BoardingPass::getSeatId)
                .collect(Collectors.toSet());
        
        List<Seat> availableSeats = allSeats.stream()
                .filter(seat -> !assignedSeatIds.contains(seat.getSeatId()))
                .collect(Collectors.toList());
        
        // Asignar asientos por grupos
        for (Map.Entry<Integer, List<BoardingPass>> groupEntry : groups.entrySet()) {
            groupSeatAssignmentService.assignSeatsForGroup(groupEntry.getValue(), availableSeats, airplaneId);
        }
    }
}
