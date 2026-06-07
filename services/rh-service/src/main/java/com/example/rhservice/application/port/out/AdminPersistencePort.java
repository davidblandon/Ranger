package com.example.rhservice.application.port.out;

import com.example.rhservice.domain.model.Admin;

/** Persistence port for Admin-specific operations. */
public interface AdminPersistencePort extends CrudPersistencePort<Admin, Long> {
}