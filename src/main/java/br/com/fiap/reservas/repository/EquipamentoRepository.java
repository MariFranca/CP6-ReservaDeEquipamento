package br.com.fiap.reservas.repository;

import br.com.fiap.reservas.entity.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    List<Equipamento> findByAtivoTrue();

    List<Equipamento> findByTipoContainingIgnoreCase(String tipo);
}
