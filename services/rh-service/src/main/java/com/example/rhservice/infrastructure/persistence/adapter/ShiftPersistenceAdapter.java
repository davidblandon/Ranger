package com.example.rhservice.infrastructure.persistence.adapter;

import com.example.rhservice.application.port.out.ShiftPersistencePort;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.infrastructure.persistence.jpa.ShiftJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShiftPersistenceAdapter implements ShiftPersistencePort {

    /** Adapter implementing {@link com.example.rhservice.application.port.out.ShiftPersistencePort}.
     *
     * Keeps mapping and repository calls concentrated in one place so application services remain framework-agnostic.
     */
    private final ShiftJpaRepository repository;

    public ShiftPersistenceAdapter(ShiftJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Shift> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Shift> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Shift save(Shift aggregate) {
        return repository.save(aggregate);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}