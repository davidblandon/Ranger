package com.example.rhservice.application.service.impl;

import com.example.rhservice.application.port.in.ShiftService;
import com.example.rhservice.application.port.out.ShiftPersistencePort;
import com.example.rhservice.domain.model.Shift;
import org.springframework.stereotype.Service;

@Service
public class ShiftServiceImpl extends AbstractCrudService<Shift, Long> implements ShiftService {

    /** Application service for Shift use-cases. */
    public ShiftServiceImpl(ShiftPersistencePort persistencePort) {
        super(persistencePort);
    }
}