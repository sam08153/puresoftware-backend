package com.puresoftware.ticketing.web;

import com.puresoftware.ticketing.dto.ReserveRequest;
import com.puresoftware.ticketing.dto.ReserveResponse;
import com.puresoftware.ticketing.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void reserve_success() throws Exception {
        ReserveRequest req = new ReserveRequest();
        req.setUserId(1L); req.setEventId(10L); req.setSeatIds(Set.of(100L, 101L));
        ReserveResponse resp = new ReserveResponse("abc", Instant.now().plusSeconds(600));
        Mockito.when(bookingService.reserveSeats(Mockito.any())).thenReturn(resp);

        String body = "{\"userId\":1,\"eventId\":10,\"seatIds\":[100,101]}";
        mockMvc.perform(post("/bookings/reserve").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value("abc"));
    }

    @Test
    void reserve_conflictReturns409() throws Exception {
        Mockito.when(bookingService.reserveSeats(Mockito.any()))
                .thenThrow(new IllegalStateException("Seat currently held"));
        String body = "{\"userId\":1,\"eventId\":10,\"seatIds\":[100]}";
        mockMvc.perform(post("/bookings/reserve").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void confirm_success() throws Exception {
        Mockito.when(bookingService.confirmReservation("abc", 1L, "PAY"))
                .thenReturn(999L);
        String body = "{\"reservationId\":\"abc\",\"userId\":1,\"paymentReference\":\"PAY\"}";
        mockMvc.perform(post("/bookings/confirm").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(999));
    }

    @Test
    void confirm_badRequestOnIllegalArgument() throws Exception {
        Mockito.when(bookingService.confirmReservation("bad", 1L, "PAY"))
                .thenThrow(new IllegalArgumentException("Reservation does not belong to user"));
        String body = "{\"reservationId\":\"bad\",\"userId\":1,\"paymentReference\":\"PAY\"}";
        mockMvc.perform(post("/bookings/confirm").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void confirm_serverErrorOnException() throws Exception {
        Mockito.when(bookingService.confirmReservation("oops", 1L, "PAY"))
                .thenThrow(new RuntimeException("Unexpected"));
        String body = "{\"reservationId\":\"oops\",\"userId\":1,\"paymentReference\":\"PAY\"}";
        mockMvc.perform(post("/bookings/confirm").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}
