package br.com.fiap.reservas.repository;

import br.com.fiap.reservas.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
