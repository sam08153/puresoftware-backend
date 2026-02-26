package com.puresoftware.ticketing.repository;

import com.puresoftware.ticketing.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where (:city is null or e.city = :city) " +
            "and (:category is null or e.category = :category) " +
            "and (:fromDate is null or e.eventDateTime >= :fromDate) " +
            "and (:toDate is null or e.eventDateTime <= :toDate)")
    List<Event> search(@Param("city") String city,
                       @Param("category") String category,
                       @Param("fromDate") LocalDateTime fromDate,
                       @Param("toDate") LocalDateTime toDate);
}

