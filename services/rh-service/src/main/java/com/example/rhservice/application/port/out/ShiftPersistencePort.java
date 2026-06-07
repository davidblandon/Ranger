package com.example.rhservice.application.port.out;

import com.example.rhservice.domain.model.Shift;

/** Persistence port for Shift entities. */
public interface ShiftPersistencePort extends CrudPersistencePort<Shift, Long> {
}