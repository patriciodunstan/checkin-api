package com.andesairlines.checkin_api.flight.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {
    private Integer flightId;
    private LocalDateTime takeoffDateTime;
    private String takeoffAirport;
    private LocalDateTime landingDateTime;
    private String landingAirport;
    private Integer airplaneId;
    private List<PassengerSeatInfo> passengers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class PassengerSeatInfo {
        private Integer passengerId;
        private String dni;
        private String name;
        private Integer age;
        private String country;
        private Integer seatTypeId;
        private Integer seatId;
        private String seatRow;
        private String seatColumn;
    }
}