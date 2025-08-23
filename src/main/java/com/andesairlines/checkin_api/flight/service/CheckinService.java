package com.andesairlines.checkin_api.flight.service;


import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import com.andesairlines.checkin_api.airplane.repository.SeatRepository;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CheckinService {

    private final FlightRepository flightRepository;
    private final BoardingPassRepository boardingPassRepository;
    private final SeatRepository seatRepository;

    @Cacheable(value = "flight", key = "#flightId")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public FlightResponse performCheckin(Integer flightId){
        log.info("Performing check-in for flight: {}", flightId);

        Flight flight = flightRepository.findByIdWithAirplane(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found with ID: " + flightId));

        List<BoardingPass> boardingPasses = boardingPassRepository.findBoardingPassesByFlightIdOrderedByPurchase(flightId);

        Map<Integer, List<BoardingPass>> groups = boardingPasses.stream()
                .collect(Collectors.groupingBy(BoardingPass::getPurchaseId));

        List<Seat> allSeats = seatRepository.findByAirplaneId(flight.getAirplaneId());

        Set<Integer> assignedSeatIds = boardingPasses.stream()
                .filter(bp -> bp.getSeatId() != null)
                .map(BoardingPass::getSeatId)
                .collect(Collectors.toSet());

        List<Seat> avaliableSeats = allSeats.stream()
                .filter(seat -> !assignedSeatIds.contains(seat.getSeatId()))
                .collect(Collectors.toList());


        for(Map.Entry<Integer, List<BoardingPass>> groupedEntry: groups.entrySet()){
            assignSeatsForGroup(groupedEntry.getValue(), avaliableSeats, flight.getAirplaneId());
        }

        List<BoardingPass> updatedBoardingPasses = boardingPassRepository.findBoardingPassesByFlightId(flightId);

        return mapToFlightResponse(flight, updatedBoardingPasses);
    }

    private void assignSeatsForGroup(List<BoardingPass> group, List<Seat> availableSeats, Integer airplaneId){
        log.debug("Assigning seats for group with {} passengers", group.size());

        List<BoardingPass> minors = group.stream()
                .filter(bp -> bp.getPassenger().getAge() < 18)
                .collect(Collectors.toList());

        List<BoardingPass> adults = group.stream()
                .filter(bp -> bp.getPassenger().getAge() >= 18)
                .collect(Collectors.toList());

        if(!minors.isEmpty() && !adults.isEmpty()){
            assignSeatWithMinors(group, minors, adults, availableSeats, airplaneId);
        }else {
            assignSeatsNormally(group, availableSeats, airplaneId);
        }
    }

    private void assignSeatWithMinors(List<BoardingPass> group, List<BoardingPass> minors, List<BoardingPass> adults, List<Seat> availableSeats, Integer airplaneId){
        log.debug("Assigning seats for group {} with minors and {} adults", minors.size(), adults.size());

        Map<Integer, List<BoardingPass>> groupBySeatType = group.stream()
                .collect(Collectors.groupingBy(BoardingPass::getSeatTypeId));

        for (Map.Entry<Integer, List<BoardingPass>> entry : groupBySeatType.entrySet()){
            Integer seatTypeId = entry.getKey();
            List<BoardingPass> passengersOfType = entry.getValue();

            List<Seat> seatsOfType = availableSeats.stream()
                    .filter(seat -> seat.getSeatTypeId().equals(seatTypeId))
                    .toList();

            Map<Integer, List<Seat>> seatsByRow = seatsOfType.stream()
                    .collect(Collectors.groupingBy(Seat::getSeatRow));

            boolean assigned = false;
            for (Map.Entry<Integer, List<Seat>> rowEntrey : seatsByRow.entrySet()){
                Integer row = rowEntrey.getKey();
                List<Seat> rowSeats = rowEntrey.getValue();

                if(rowSeats.size() >= passengersOfType.size()){
                    rowSeats.sort(Comparator.comparing(Seat::getSeatColumn));

                    if(hasConsecutiveSeats(rowSeats, passengersOfType.size())){
                        assignConsecutiveSeats(passengersOfType, rowSeats, availableSeats);
                        assigned = true;
                        break;
                    }
                }
            }

            if(!assigned){
                assignBestAvailableSeats(passengersOfType, availableSeats, availableSeats);
            }
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

            // Filtrar asientos disponibles del tipo correcto
            List<Seat> seatsOfType = availableSeats.stream()
                    .filter(seat -> seat.getSeatTypeId().equals(seatTypeId))
                    .collect(Collectors.toList());

            // Intentar asignar consecutivos primero
            boolean assigned = false;
            Map<Integer, List<Seat>> seatsByRow = seatsOfType.stream()
                    .collect(Collectors.groupingBy(Seat::getSeatRow));

            for (Map.Entry<Integer, List<Seat>> rowEntry : seatsByRow.entrySet()) {
                List<Seat> rowSeats = rowEntry.getValue();
                if (rowSeats.size() >= passengersOfType.size() && hasConsecutiveSeats(rowSeats, passengersOfType.size())) {
                    rowSeats.sort(Comparator.comparing(Seat::getSeatColumn));
                    assignConsecutiveSeats(passengersOfType, rowSeats, availableSeats);
                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                assignBestAvailableSeats(passengersOfType, seatsOfType, availableSeats);
            }
        }
    }

    private boolean hasConsecutiveSeats(List<Seat> seats, int requiredCount){
        if (seats.size() < requiredCount) return  false;

        seats.sort(Comparator.comparing(Seat::getSeatColumn));

        for (int i = 0; i <= seats.size() -requiredCount; i++){
            boolean consecutive = true;
            for(int j = 0; j < requiredCount -1; j++){
                String currentColumn = seats.get(i + j).getSeatColumn();
                String nextColumn = seats.get(i + j).getSeatColumn();

                if(!areConsecutiveColumns(currentColumn, nextColumn)){
                    consecutive = false;
                    break;
                }
            }
            if(consecutive) return true;
        }
        return false;
    }

    private boolean areConsecutiveColumns(String col1, String col2){
        return Math.abs(col1.charAt(0) - col2.charAt(0)) == 1;
    }

    private void assignConsecutiveSeats(List<BoardingPass> passengers, List<Seat> rowSeats, List<Seat> availableSeats){
        rowSeats.sort(Comparator.comparing(Seat::getSeatColumn));

        for (int i = 0; i < passengers.size(); i++){
            Seat seat = rowSeats.get(i);
            BoardingPass boardingPass = passengers.get(i);

            boardingPass.setSeatId(seat.getSeatId());
            boardingPassRepository.save(boardingPass);

            availableSeats.removeIf(s -> s.getSeatId().equals(seat.getSeatId()));
        }
    }

    private void assignBestAvailableSeats(List<BoardingPass> passengers, List<Seat> availableSeatsOfType, List<Seat> availableSeats) {

        availableSeatsOfType.sort(Comparator.comparing(Seat::getSeatRow).thenComparing(Seat::getSeatColumn));

        for (int i = 0; i < passengers.size() && i < availableSeatsOfType.size(); i++) {
            Seat seat = availableSeatsOfType.get(i);
            BoardingPass boardingPass = passengers.get(i);

            boardingPass.setSeatId(seat.getSeatId());
            boardingPassRepository.save(boardingPass);


            availableSeats.removeIf(s -> s.getSeatId().equals(seat.getSeatId()));
        }
    }

    private FlightResponse mapToFlightResponse(Flight flight, List<BoardingPass> boardingPasses) {
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
                            .map(bp -> {
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
                            })
                            .toList()
            );
        }

        return response;
    }


}
