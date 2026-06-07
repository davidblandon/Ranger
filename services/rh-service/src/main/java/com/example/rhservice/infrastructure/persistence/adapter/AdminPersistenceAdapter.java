package com.example.rhservice.infrastructure.persistence.adapter;

import com.example.rhservice.application.port.out.AdminPersistencePort;
import com.example.rhservice.domain.model.Admin;
import com.example.rhservice.infrastructure.persistence.jpa.AdminJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminPersistenceAdapter implements AdminPersistencePort {

    /** Adapter that implements {@link com.example.rhservice.application.port.out.AdminPersistencePort}.
     *
     * Keeps Spring Data JPA concerns isolated from the application layer; maps calls to the repository.
     */
    private final AdminJpaRepository repository;

    public AdminPersistenceAdapter(AdminJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Admin> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Admin save(Admin aggregate) {
        return repository.save(aggregate);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}