package com.andesairlines.checkin_api.airplane.repository;


import com.andesairlines.checkin_api.airplane.model.entity.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Integer> {
}
