package com.andesairlines.checkin_api.seat.repository;

import com.andesairlines.checkin_api.seat.model.entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Integer> {
    Optional<SeatType> findByName(String name);
}
