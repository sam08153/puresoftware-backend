package com.puresoftware.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmRequest {
    @NotBlank
    private String reservationId;
    @NotNull
    private Long userId;
    @NotBlank
    private String paymentReference;
}

