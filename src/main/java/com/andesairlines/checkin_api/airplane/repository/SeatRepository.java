package com.andesairlines.checkin_api.airplane.repository;

import com.andesairlines.checkin_api.airplane.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    List<Seat> findByAirplaneIdAndSeatTypeId(Integer airplaneId, Integer seatTypeId);

    @Query("SELECT s FROM Seat s WHERE s.airplaneId = :airplaneId AND s.seatTypeId = :seatTypeId AND s.seatId NOT IN (SELECT COALESCE(bp.satId, 0) FROM BoardingPass bp WHERE bp.flightId = :flightId AND bp.seatId IS NOT NULL)")
    List<Seat> findAvailableSeatsByAirplaneAndType(@Param("airplaneId") Integer airplaneId, @Param("seatTypeId") Integer seatTypeId, @Param("flightId") Integer flightId);

    Optional<Seat> findBySeatRowAndSeatColumnAndAirplaneId(Integer seatRow, String seatColumn, Integer airplaneId);

}
