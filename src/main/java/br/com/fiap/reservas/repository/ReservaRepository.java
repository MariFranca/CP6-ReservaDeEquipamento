package br.com.fiap.reservas.repository;

import br.com.fiap.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findBySala_Id(Long salaId);

    /**
     * Regras de negócio 2 e 3 (conflito entre reservas / disponibilidade
     * da sala): retorna as reservas já existentes para a mesma sala cujo
     * período informado se sobrepõe ao período pesquisado.
     *
     * Dois intervalos [retirada, entrega) se sobrepõem quando:
     *   retiradaExistente < entregaNova  E  entregaExistente > retiradaNova
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.sala.id = :salaId
              AND r.horarioRetirada < :horarioEntrega
              AND r.horarioEntrega > :horarioRetirada
            """)
    List<Reserva> buscarConflitosDeSala(@Param("salaId") Long salaId,
                                         @Param("horarioRetirada") LocalDateTime horarioRetirada,
                                         @Param("horarioEntrega") LocalDateTime horarioEntrega);

    /**
     * Regra de negócio 1 (disponibilidade do equipamento): retorna as
     * reservas já existentes que incluem o equipamento informado e cujo
     * período se sobrepõe ao período pesquisado.
     */
    @Query("""
            SELECT r FROM Reserva r
            JOIN r.equipamentos e
            WHERE e.id = :equipamentoId
              AND r.horarioRetirada < :horarioEntrega
              AND r.horarioEntrega > :horarioRetirada
            """)
    List<Reserva> buscarConflitosDeEquipamento(@Param("equipamentoId") Long equipamentoId,
                                                @Param("horarioRetirada") LocalDateTime horarioRetirada,
                                                @Param("horarioEntrega") LocalDateTime horarioEntrega);
}
