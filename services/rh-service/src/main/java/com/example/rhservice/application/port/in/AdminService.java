package com.example.rhservice.application.port.in;

import com.example.rhservice.domain.model.Admin;

/** Use-case interface for admin operations. Controllers should depend on this interface, not implementations. */
public interface AdminService extends CrudUseCase<Admin, Long> {
}