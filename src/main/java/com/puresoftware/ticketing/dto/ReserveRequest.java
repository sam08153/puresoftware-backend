package com.puresoftware.ticketing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class ReserveRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long eventId;
    @NotEmpty
    private Set<Long> seatIds;
}

