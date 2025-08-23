package com.andesairlines.checkin_api.flight.service;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import com.andesairlines.checkin_api.flight.repository.BoardingPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConsecutiveSeatAssignmentService {
    
    private final BoardingPassRepository boardingPassRepository;
    
    public void assignSeatsForPassengers(List<BoardingPass> passengers, List<Seat> availableSeats, Integer seatTypeId) {
        // Filtrar asientos disponibles del tipo correcto
        List<Seat> seatsOfType = availableSeats.stream()
                .filter(seat -> seat.getSeatTypeId().equals(seatTypeId))
                .collect(Collectors.toList());
        
        // Buscar filas con suficientes asientos consecutivos
        Map<Integer, List<Seat>> seatsByRow = seatsOfType.stream()
                .collect(Collectors.groupingBy(Seat::getSeatRow));
        
        boolean assigned = false;
        for (Map.Entry<Integer, List<Seat>> rowEntry : seatsByRow.entrySet()) {
            List<Seat> rowSeats = rowEntry.getValue();
            
            if (rowSeats.size() >= passengers.size()) {
                // Ordenar asientos por columna
                rowSeats.sort(Comparator.comparing(Seat::getSeatColumn));
                
                // Verificar que hay suficientes asientos consecutivos
                if (hasConsecutiveSeats(rowSeats, passengers.size())) {
                    assignConsecutiveSeats(passengers, rowSeats, availableSeats);
                    assigned = true;
                    break;
                }
            }
        }
        
        if (!assigned) {
            // Si no se pueden asignar consecutivos, asignar los mejores disponibles
            assignBestAvailableSeats(passengers, seatsOfType, availableSeats);
        }
    }
    
    private boolean hasConsecutiveSeats(List<Seat> seats, int requiredCount) {
        if (seats.size() < requiredCount) return false;
        
        seats.sort(Comparator.comparing(Seat::getSeatColumn));
        
        for (int i = 0; i <= seats.size() - requiredCount; i++) {
            boolean consecutive = true;
            for (int j = 0; j < requiredCount - 1; j++) {
                String currentCol = seats.get(i + j).getSeatColumn();
                String nextCol = seats.get(i + j + 1).getSeatColumn();
                
                // Verificar que las columnas sean consecutivas (A-B, B-C, etc.)
                if (!areConsecutiveColumns(currentCol, nextCol)) {
                    consecutive = false;
                    break;
                }
            }
            if (consecutive) return true;
        }
        return false;
    }
    
    private boolean areConsecutiveColumns(String col1, String col2) {
        return Math.abs(col1.charAt(0) - col2.charAt(0)) == 1;
    }
    
    private void assignConsecutiveSeats(List<BoardingPass> passengers, List<Seat> rowSeats, List<Seat> availableSeats) {
        rowSeats.sort(Comparator.comparing(Seat::getSeatColumn));
        
        for (int i = 0; i < passengers.size(); i++) {
            Seat seat = rowSeats.get(i);
            BoardingPass boardingPass = passengers.get(i);
            
            boardingPass.setSeatId(seat.getSeatId());
            boardingPassRepository.save(boardingPass);
            
            // Remover asiento de disponibles
            availableSeats.removeIf(s -> s.getSeatId().equals(seat.getSeatId()));
        }
    }
    
    private void assignBestAvailableSeats(List<BoardingPass> passengers, List<Seat> availableSeatsOfType, List<Seat> availableSeats) {
        // Ordenar asientos por fila y columna para asignación consistente
        availableSeatsOfType.sort(Comparator.comparing(Seat::getSeatRow).thenComparing(Seat::getSeatColumn));
        
        for (int i = 0; i < passengers.size() && i < availableSeatsOfType.size(); i++) {
            Seat seat = availableSeatsOfType.get(i);
            BoardingPass boardingPass = passengers.get(i);
            
            boardingPass.setSeatId(seat.getSeatId());
            boardingPassRepository.save(boardingPass);
            
            // Remover asiento de disponibles
            availableSeats.removeIf(s -> s.getSeatId().equals(seat.getSeatId()));
        }
    }
}
