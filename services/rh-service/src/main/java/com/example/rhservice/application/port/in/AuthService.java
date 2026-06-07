package com.example.rhservice.application.port.in;

/** Use case contract for authentication. */
public interface AuthService {

    AuthSession login(String username, String password);
}