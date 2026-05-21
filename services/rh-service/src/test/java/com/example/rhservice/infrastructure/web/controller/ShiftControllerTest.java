package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.ShiftService;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.domain.model.TimeBlock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShiftController.class)
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShiftService shiftService;

    @Test
    void shouldListShifts() throws Exception {
        when(shiftService.findAll()).thenReturn(List.of(buildShift(1L)));

        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldCreateShift() throws Exception {
        when(shiftService.save(any(Shift.class))).thenAnswer(invocation -> {
            Shift shift = invocation.getArgument(0);
            shift.setId(2L);
            return shift;
        });

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monday":[{"start":"08:00:00","end":"12:00:00"}],"tuesday":[],"wednesday":[],"thursday":[],"friday":[],"saturday":[],"sunday":[]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.monday[0].start").value("08:00:00"));
    }

    @Test
    void shouldGetShiftById() throws Exception {
        when(shiftService.findById(1L)).thenReturn(java.util.Optional.of(buildShift(1L)));

        mockMvc.perform(get("/api/shifts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateShift() throws Exception {
        when(shiftService.findById(1L)).thenReturn(java.util.Optional.of(buildShift(1L)));
        when(shiftService.save(any(Shift.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/shifts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monday":[],"tuesday":[],"wednesday":[],"thursday":[],"friday":[],"saturday":[],"sunday":[]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldDeleteShift() throws Exception {
        mockMvc.perform(delete("/api/shifts/1"))
                .andExpect(status().isNoContent());

        verify(shiftService).delete(1L);
    }

    private Shift buildShift(Long id) {
        Shift shift = new Shift();
        shift.setId(id);
        shift.setMonday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(12, 0))));
        shift.setTuesday(List.of());
        shift.setWednesday(List.of());
        shift.setThursday(List.of());
        shift.setFriday(List.of());
        shift.setSaturday(List.of());
        shift.setSunday(List.of());
        return shift;
    }
}