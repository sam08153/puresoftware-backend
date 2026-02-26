package com.puresoftware.ticketing.repository;

import com.puresoftware.ticketing.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}

