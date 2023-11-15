package org.develop.TeamProjectPanaderia.personal.repositories;

import org.develop.TeamProjectPanaderia.categoria.models.Categoria;
import org.develop.TeamProjectPanaderia.personal.models.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, UUID>, JpaSpecificationExecutor<Personal> {
    Optional<Personal> findById(UUID uuid);
    List<Personal> findByIsActive(boolean isActive);
    Optional<Personal> findByDniEqualsIgnoreCase(String dni);
}

