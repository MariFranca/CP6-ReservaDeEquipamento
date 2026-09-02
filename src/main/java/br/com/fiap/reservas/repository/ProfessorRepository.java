package br.com.fiap.reservas.repository;

import br.com.fiap.reservas.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
