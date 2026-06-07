package com.example.rhservice.infrastructure.persistence.adapter;

import com.example.rhservice.application.port.out.PayrollPersistencePort;
import com.example.rhservice.domain.model.Payroll;
import com.example.rhservice.infrastructure.persistence.jpa.PayrollJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PayrollPersistenceAdapter implements PayrollPersistencePort {

    /** Adapter implementing {@link com.example.rhservice.application.port.out.PayrollPersistencePort}.
     *
     * Responsible for all persistence operations related to {@link com.example.rhservice.domain.model.Payroll}.
     */
    private final PayrollJpaRepository repository;

    public PayrollPersistenceAdapter(PayrollJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Payroll> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Payroll> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Payroll save(Payroll aggregate) {
        return repository.save(aggregate);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}