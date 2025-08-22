package com.andesairlines.checkin_api.airplane.model.dto;

import java.util.List;

public class AirplaneDto {

    private Long airplaneId;
    private String name;
    private List<SeatDto> seats;

    public AirplaneDto(Long airplaneId, String name, List<SeatDto> seats) {
        this.airplaneId = airplaneId;
        this.name = name;
        this.seats = seats;
    }

    public Long getAirplaneId() {
        return airplaneId;
    }

    public void setAirplaneId(Long airplaneId) {
        this.airplaneId = airplaneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDto> seats) {
        this.seats = seats;
    }
}
