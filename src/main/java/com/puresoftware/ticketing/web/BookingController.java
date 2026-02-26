package com.puresoftware.ticketing.web;

import com.puresoftware.ticketing.dto.ConfirmRequest;
import com.puresoftware.ticketing.dto.ReserveRequest;
import com.puresoftware.ticketing.dto.ReserveResponse;
import com.puresoftware.ticketing.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/reserve")
    public ResponseEntity<ReserveResponse> reserve(@Valid @RequestBody ReserveRequest request) {
        return ResponseEntity.ok(bookingService.reserveSeats(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Long> confirm(@Valid @RequestBody ConfirmRequest request) {
        Long id = bookingService.confirmReservation(request.getReservationId(), request.getUserId(), request.getPaymentReference());
        return ResponseEntity.ok(id);
    }
}

