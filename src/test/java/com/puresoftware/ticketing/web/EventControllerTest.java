package com.puresoftware.ticketing.web;

import com.puresoftware.ticketing.domain.Event;
import com.puresoftware.ticketing.domain.Seat;
import com.puresoftware.ticketing.service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void listEvents_returnsDtos() throws Exception {
        Event e = Event.builder()
                .id(10L).title("Rock Night").category("Music").city("Pune")
                .eventDateTime(LocalDateTime.of(2030, 1, 2, 20, 0))
                .build();
        Mockito.when(eventService.search(null, null, null, null)).thenReturn(List.of(e));

        mockMvc.perform(get("/events").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title").value("Rock Night"))
                .andExpect(jsonPath("$[0].category").value("Music"))
                .andExpect(jsonPath("$[0].city").value("Pune"));
    }

    @Test
    void search_withParams_invokesService() throws Exception {
        Mockito.when(eventService.search("Pune", "Music", null, null)).thenReturn(List.of());
        mockMvc.perform(get("/events?city=Pune&category=Music").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(eventService).search("Pune", "Music", null, null);
    }

    @Test
    void listSeats_returnsDtos() throws Exception {
        Event e = Event.builder().id(10L).build();
        Seat s = Seat.builder()
                .id(100L).event(e).rowLabel("A").seatNumber(1).tier("VIP")
                .price(new BigDecimal("1500.00")).status(Seat.Status.AVAILABLE)
                .build();
        Mockito.when(eventService.getSeats(10L)).thenReturn(List.of(s));

        mockMvc.perform(get("/events/10/seats").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].rowLabel").value("A"))
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].tier").value("VIP"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }
}
