package com.example.rhservice.application.port.in;

import com.example.rhservice.domain.model.Shift;

/** Use-case interface for shift operations (CRUD). */
public interface ShiftService extends CrudUseCase<Shift, Long> {
}