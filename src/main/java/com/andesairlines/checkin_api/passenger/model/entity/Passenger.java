package com.andesairlines.checkin_api.passenger.model.entity;


import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "passenger")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Integer passengerId;

    @Column(name = "dni", nullable = false, unique = true, length = 20)
    private String dni;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "boarding_pass_id")
    private Integer boardingPassId;

    @Column(name = "seat_type_id", nullable = false)
    private Integer seatTypeId;

    @Column(name = "seat_id")
    private Integer seatId;

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardingPass> boardingPasses;
}
