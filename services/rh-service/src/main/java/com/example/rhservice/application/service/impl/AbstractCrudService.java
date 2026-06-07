package com.example.rhservice.application.service.impl;

import com.example.rhservice.application.port.in.CrudUseCase;
import com.example.rhservice.application.port.out.CrudPersistencePort;

import java.util.List;
import java.util.Optional;

public abstract class AbstractCrudService<T, ID> implements CrudUseCase<T, ID> {
    /** Persistence port (outbound) injected via constructor. */
    private final CrudPersistencePort<T, ID> persistencePort;

    /**
     * Protected constructor to enforce that subclasses supply the correct persistence adapter.
     * This demonstrates Dependency Inversion: the service depends on an abstraction (port), not a concrete repository.
     */
    protected AbstractCrudService(CrudPersistencePort<T, ID> persistencePort) {
        this.persistencePort = persistencePort;
    }

    /** Return all entities by delegating to the persistence port. */
    @Override
    public List<T> findAll() {
        return persistencePort.findAll();
    }

    /** Find by id using the persistence port. */
    @Override
    public Optional<T> findById(ID id) {
        return persistencePort.findById(id);
    }

    /** Save or update entity through the persistence port. */
    @Override
    public T save(T aggregate) {
        return persistencePort.save(aggregate);
    }

    /** Delete an entity by its id via the persistence port. */
    @Override
    public void delete(ID id) {
        persistencePort.deleteById(id);
    }
}