package com.example.rhservice.domain.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "shifts")
/**
 * Shift entity capturing weekly schedules.
 *
 * <p>Each weekday stores a list of {@link TimeBlock} intervals. {@code @ElementCollection}
 * is used for value-type persistence of the embedded {@link TimeBlock} objects.
 */
public class Shift {

    /** Primary key for shift. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Time blocks for Monday. */
    @ElementCollection
    private List<TimeBlock> monday = new ArrayList<>();

    /** Time blocks for Tuesday. */
    @ElementCollection
    private List<TimeBlock> tuesday = new ArrayList<>();

    /** Time blocks for Wednesday. */
    @ElementCollection
    private List<TimeBlock> wednesday = new ArrayList<>();

    /** Time blocks for Thursday. */
    @ElementCollection
    private List<TimeBlock> thursday = new ArrayList<>();

    /** Time blocks for Friday. */
    @ElementCollection
    private List<TimeBlock> friday = new ArrayList<>();

    /** Time blocks for Saturday. */
    @ElementCollection
    private List<TimeBlock> saturday = new ArrayList<>();

    /** Time blocks for Sunday. */
    @ElementCollection
    private List<TimeBlock> sunday = new ArrayList<>();
}