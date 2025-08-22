package com.andesairlines.checkin_api.airplane.repository;


import com.andesairlines.checkin_api.airplane.model.entity.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Integer> {
    @Query("SELECT a FROM airplane a LEFT JOIN FETCH a.seats WHERE a.airplaneId =: airplaneId")
    Optional<Airplane> findByIdWithSeats(@Param("airplaneId") Integer airplaneId);
}
