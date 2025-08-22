package com.andesairlines.checkin_api.flight.repository;

import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BoardingPassRepository extends JpaRepository<BoardingPass, Integer> {

    List<BoardingPass> findByFlightId(Integer flightId);

    @Query("SELECT bp FROM BoardingPass bp LEFT JOIN FETCH bp.passenger LEFT JOIN FETCH bp.seat WHERE bp.flightId = :flightId")
    List<BoardingPass> findBoardingPassesByFlightId(@Param("flightId") Integer flightId);

    Optional<BoardingPass> findByFlightIdAndPassengerId(Integer flightId, Integer passengerId);

    @Query("SELECT bp FROM BoardingPass bp WHERE bp.flightId = :flightId AND bp.seatId IS NOT NULL")
    List<BoardingPass> findAssignedSeatsByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT bp FROM BoardingPass bp LEFT JOIN FETCH bp.passenger WHERE bp.flightId = :flightId ORDER BY bp.purchaseId")
    List<BoardingPass> findBoardingPassesByFlightIdOrderedByPurchase(@Param("flightId") Integer flightId);

    @Query("SELECT bp FROM BoardingPass bp LEFT JOIN FETCH bp.passenger WHERE bp.flightId = :flightId AND bp.purchaseId = :purchaseId")
    List<BoardingPass> findByFlightIdAndPurchaseId(@Param("flightId") Integer flightId, @Param("purchaseId") Integer purchaseId);
}
