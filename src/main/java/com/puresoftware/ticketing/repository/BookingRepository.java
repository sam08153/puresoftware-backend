package com.puresoftware.ticketing.repository;

import com.puresoftware.ticketing.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}

