package br.com.fiap.reservas.repository;

import br.com.fiap.reservas.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}
