package com.andesairlines.checkin_api.passenger.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerResponse {
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