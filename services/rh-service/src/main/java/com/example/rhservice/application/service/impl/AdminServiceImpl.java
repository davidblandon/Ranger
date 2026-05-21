package com.example.rhservice.application.service.impl;

import com.example.rhservice.application.port.in.AdminService;
import com.example.rhservice.application.port.out.AdminPersistencePort;
import com.example.rhservice.domain.model.Admin;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends AbstractCrudService<Admin, Long> implements AdminService {

    /** Application service for Admin use-cases. Delegates persistence to the AdminPersistencePort. */
    public AdminServiceImpl(AdminPersistencePort persistencePort) {
        super(persistencePort);
    }
}