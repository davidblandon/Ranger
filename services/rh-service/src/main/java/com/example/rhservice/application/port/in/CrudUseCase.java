package com.example.rhservice.application.port.in;

import java.util.List;
import java.util.Optional;

public interface CrudUseCase<T, ID> {
    /**
     * Return all aggregates of type T.
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Find an aggregate by its identifier.
     *
     * @param id the identifier
     * @return optional containing the entity when found
     */
    Optional<T> findById(ID id);

    /**
     * Save or update an aggregate.
     *
     * @param aggregate entity to persist
     * @return persisted entity (may include generated id)
     */
    T save(T aggregate);

    /**
     * Delete an aggregate by id.
     *
     * @param id identifier of entity to remove
     */
    void delete(ID id);
}