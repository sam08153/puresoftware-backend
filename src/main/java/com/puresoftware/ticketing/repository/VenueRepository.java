package com.puresoftware.ticketing.repository;

import com.puresoftware.ticketing.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}

