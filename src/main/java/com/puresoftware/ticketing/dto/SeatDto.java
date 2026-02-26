package com.puresoftware.ticketing.dto;

import com.puresoftware.ticketing.domain.Seat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private String rowLabel;
    private Integer seatNumber;
    private String tier;
    private BigDecimal price;
    private Seat.Status status;

    public static SeatDto from(Seat s) {
        return new SeatDto(s.getId(), s.getRowLabel(), s.getSeatNumber(), s.getTier(), s.getPrice(), s.getStatus());
    }
}
