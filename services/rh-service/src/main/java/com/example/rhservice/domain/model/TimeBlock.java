package com.example.rhservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TimeBlock {
    /** Start time for the interval (inclusive). */
    @Column(name = "start_time")
    private LocalTime start;

    /** End time for the interval (exclusive). */
    @Column(name = "end_time")
    private LocalTime end;
}