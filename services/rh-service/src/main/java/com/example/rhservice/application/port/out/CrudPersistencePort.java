package com.example.rhservice.application.port.out;

import java.util.List;
import java.util.Optional;

public interface CrudPersistencePort<T, ID> {
    /**
     * Retrieve all persisted entities of type T.
     * Implementations are responsible for mapping between domain and persistence models if needed.
     */
    List<T> findAll();

    /** Find by id. */
    Optional<T> findById(ID id);

    /** Save or update entity in the underlying store. */
    T save(T aggregate);

    /** Delete an entity by id from the store. */
    void deleteById(ID id);
}