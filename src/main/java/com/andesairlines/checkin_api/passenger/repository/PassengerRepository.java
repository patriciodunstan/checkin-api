package com.andesairlines.checkin_api.passenger.repository;

import com.andesairlines.checkin_api.passenger.model.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    Optional<Passenger> findByDni(String dni);

    @Query("SELECT p FROM Passenger p LEFT JOIN FETCH p.boardingPasses WHERE p.passengerId = :passengerId")
    Optional<Passenger> findByIdWithBoardingPasses(@Param("passengerId") Integer passengerId);

    List<Passenger> findBySeatTypeId(Integer seatTypeId);
}