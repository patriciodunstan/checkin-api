package com.andesairlines.checkin_api.airplane.model.dto;

public class SeatDto {
    private Long seatId;
    private String seatColumn;
    private Integer seatRow;
    private Long seatTypeId;
    private Long airplaneId;
    private boolean occupied;

    public SeatDto(Long seatId, String seatColumn, Integer seatRow, Long seatTypeId, Long airplaneId, boolean occupied) {
        this.seatId = seatId;
        this.seatColumn = seatColumn;
        this.seatRow = seatRow;
        this.seatTypeId = seatTypeId;
        this.airplaneId = airplaneId;
        this.occupied = occupied;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(String seatColumn) {
        this.seatColumn = seatColumn;
    }

    public Integer getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Integer seatRow) {
        this.seatRow = seatRow;
    }

    public Long getSeatTypeId() {
        return seatTypeId;
    }

    public void setSeatTypeId(Long seatTypeId) {
        this.seatTypeId = seatTypeId;
    }

    public Long getAirplaneId() {
        return airplaneId;
    }

    public void setAirplaneId(Long airplaneId) {
        this.airplaneId = airplaneId;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
