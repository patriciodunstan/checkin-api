package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupSeatAssignmentService {
    
    private final BoardingPassRepository boardingPassRepository;
    private final ConsecutiveSeatAssignmentService consecutiveSeatAssignmentService;
    
    public void assignSeatsForGroup(List<BoardingPass> group, List<Seat> availableSeats, Integer airplaneId) {
        log.debug("Assigning seats for group with {} passengers", group.size());
        
        // Separar menores y adultos
        List<BoardingPass> minors = group.stream()
                .filter(bp -> bp.getPassenger().getAge() < 18)
                .collect(Collectors.toList());
        
        List<BoardingPass> adults = group.stream()
                .filter(bp -> bp.getPassenger().getAge() >= 18)
                .collect(Collectors.toList());
        
        // Si hay menores, asegurar que estén junto a adultos
        if (!minors.isEmpty() && !adults.isEmpty()) {
            assignSeatsWithMinors(group, minors, adults, availableSeats, airplaneId);
        } else {
            // Grupo sin menores, asignar normalmente
            assignSeatsNormally(group, availableSeats, airplaneId);
        }
    }
    
    private void assignSeatsWithMinors(List<BoardingPass> group, List<BoardingPass> minors, 
                                     List<BoardingPass> adults, List<Seat> availableSeats, Integer airplaneId) {
        log.debug("Assigning seats for group with {} minors and {} adults", minors.size(), adults.size());
        
        // Agrupar por tipo de asiento
        Map<Integer, List<BoardingPass>> groupBySeatType = group.stream()
                .collect(Collectors.groupingBy(BoardingPass::getSeatTypeId));
        
        for (Map.Entry<Integer, List<BoardingPass>> entry : groupBySeatType.entrySet()) {
            Integer seatTypeId = entry.getKey();
            List<BoardingPass> passengersOfType = entry.getValue();
            
            consecutiveSeatAssignmentService.assignSeatsForPassengers(passengersOfType, availableSeats, seatTypeId);
        }
    }
    
    private void assignSeatsNormally(List<BoardingPass> group, List<Seat> availableSeats, Integer airplaneId) {
        log.debug("Assigning seats normally for group with {} passengers", group.size());
        
        // Agrupar por tipo de asiento
        Map<Integer, List<BoardingPass>> groupBySeatType = group.stream()
                .collect(Collectors.groupingBy(BoardingPass::getSeatTypeId));
        
        for (Map.Entry<Integer, List<BoardingPass>> entry : groupBySeatType.entrySet()) {
            Integer seatTypeId = entry.getKey();
            List<BoardingPass> passengersOfType = entry.getValue();
            
            consecutiveSeatAssignmentService.assignSeatsForPassengers(passengersOfType, availableSeats, seatTypeId);
        }
    }
}
