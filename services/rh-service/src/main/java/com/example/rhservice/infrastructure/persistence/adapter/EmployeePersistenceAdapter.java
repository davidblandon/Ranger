package com.example.rhservice.infrastructure.persistence.adapter;

import com.example.rhservice.application.port.out.EmployeePersistencePort;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.infrastructure.persistence.jpa.EmployeeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeePersistenceAdapter implements EmployeePersistencePort {

    /**
     * Adapter implementing the {@link com.example.rhservice.application.port.out.EmployeePersistencePort}.
     *
     * <p>Responsibility: translate application calls into JPA repository operations. This class keeps
     * Spring Data details out of the application layer (Single Responsibility).
     */
    private final EmployeeJpaRepository repository;

    /** Constructor injection keeps dependencies explicit and testable. */
    public EmployeePersistenceAdapter(EmployeeJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Employee save(Employee aggregate) {
        return repository.save(aggregate);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
