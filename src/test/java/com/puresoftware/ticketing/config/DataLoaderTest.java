package com.puresoftware.ticketing.config;

import com.puresoftware.ticketing.repository.EventRepository;
import com.puresoftware.ticketing.repository.SeatRepository;
import com.puresoftware.ticketing.repository.UserRepository;
import com.puresoftware.ticketing.repository.VenueRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DataLoaderTest {
    @Test
    void run_skipsWhenEventsExist() throws Exception {
        UserRepository ur = mock(UserRepository.class);
        VenueRepository vr = mock(VenueRepository.class);
        EventRepository er = mock(EventRepository.class);
        SeatRepository sr = mock(SeatRepository.class);
        when(er.count()).thenReturn(1L);
        DataLoader dl = new DataLoader(ur, vr, er, sr);
        dl.run();
        verify(er, never()).save(any());
        verify(sr, never()).saveAll(anyList());
    }
}
