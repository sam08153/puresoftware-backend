package com.puresoftware.ticketing.repository;

import com.puresoftware.ticketing.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Set;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEventId(Long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id in :ids")
    List<Seat> lockAllByIds(@Param("ids") Set<Long> ids);
}

