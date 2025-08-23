package com.andesairlines.checkin_api.flight.model.entity;

import com.andesairlines.checkin_api.airplane.model.entity.Airplane;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "takeoff_date_time", nullable = false)
    private Integer takeoffDateTime;

    @Column(name = "takeoff_airport", nullable = false, length = 3)
    private String takeoffAirport;

    @Column(name = "landing_date_time", nullable = false)
    private Integer landingDateTime;

    @Column(name = "landing_airport", nullable = false, length = 3)
    private String landingAirport;

    @Column(name = "airplane_id", nullable = false)
    private Integer airplaneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airplane_id", insertable = false, updatable = false)
    private Airplane airplane;
}