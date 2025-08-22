package com.andesairlines.checkin_api.seat.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatType {

    @Id
    @Column(name = "seat_type_id")
    private Integer seatTypeId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

}
