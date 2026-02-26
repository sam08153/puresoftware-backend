package com.puresoftware.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ReserveResponse {
    private String reservationId;
    private Instant expiresAt;
}

