package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.common.exception.NotFoundException;
import com.andesairlines.checkin_api.flight.model.dto.FlightResponse;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.model.entity.Flight;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import com.andesairlines.checkin_api.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckinService {
    
    private final FlightRepository flightRepository;
    private final BoardingPassRepository boardingPassRepository;
    private final SeatAssignmentService seatAssignmentService;
    private final FlightResponseMapper flightResponseMapper;
    
    @Cacheable(value = "flights", key = "#flightId")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public FlightResponse performCheckin(Integer flightId) {
        log.info("Performing check-in for flight: {}", flightId);
        
        // 1. Obtener el vuelo
        Flight flight = flightRepository.findByIdWithAirplane(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found with ID: " + flightId));
        
        // 2. Obtener todos los boarding passes del vuelo
        List<BoardingPass> boardingPasses = boardingPassRepository.findBoardingPassesByFlightIdOrderedByPurchase(flightId);
        
        // 3. Agrupar por purchase_id
        Map<Integer, List<BoardingPass>> groups = boardingPasses.stream()
                .collect(Collectors.groupingBy(BoardingPass::getPurchaseId));
        
        // 4. Asignar asientos por grupos
        seatAssignmentService.assignSeatsForAllGroups(groups, flight.getAirplaneId());
        
        // 5. Obtener boarding passes actualizados
        List<BoardingPass> updatedBoardingPasses = boardingPassRepository.findBoardingPassesByFlightId(flightId);
        
        return flightResponseMapper.mapToFlightResponse(flight, updatedBoardingPasses);
    }
}
