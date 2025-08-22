package com.andesairlines.checkin_api.airplane.model.entity;

import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "seat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Integer seatId;

    @Column(name = "seat_column", nullable = false, length = 1)
    private String seatColumn;

    @Column(name = "seat_row", nullable = false)
    private Integer seatRow;

    @Column(name = "seat_type_id", nullable = false)
    private Integer seatTypeId;

    @Column(name = "airplane_id", nullable = false)
    private Integer airplaneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airplane_id", insertable = false, updatable = false)
    private Airplane airplane;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardingPass> boardingPasses;
}
