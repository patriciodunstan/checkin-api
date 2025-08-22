package com.andesairlines.checkin_api.flight.repository;

import com.andesairlines.checkin_api.flight.model.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.airplane LEFT JOIN FETCH f.boardingPasses bp LEFT JOIN FETCH bp.passenger LEFT JOIN FETCH bp.seat WHERE f.flightId = :flightId")
    Optional<Flight> findByIdWithDetails(@Param("flightId") Integer flightId);

    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.airplane WHERE f.flightId = :flightId")
    Optional<Flight> findByIdWithAirplane(@Param("flightId") Integer flightId);
}
