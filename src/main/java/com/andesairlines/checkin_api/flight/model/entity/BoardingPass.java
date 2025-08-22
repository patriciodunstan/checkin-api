package com.andesairlines.checkin_api.flight.model.entity;


import com.andesairlines.checkin_api.passenger.model.entity.Passenger;
import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boarding_pass")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardingPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boarding_pass_id")
    private Integer boardingPassId;

    @Column(name = "flight_id", nullable = false)
    private Integer flightId;

    @Column(name = "passenger_id", nullable = false)
    private Integer passengerId;

    @Column(name = "seat_type_id", nullable = false)
    private Integer seatTypeId;

    @Column(name = "seat_id")
    private Integer seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", insertable = false, updatable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    private Seat seat;
}