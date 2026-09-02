package br.com.fiap.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * ENTIDADE RESERVA
 * ============================================================
 *
 * Representa a reserva de um conjunto de equipamentos, feita por um
 * professor, para uma sala e um periodo (horario de retirada ate o
 * horario de entrega).
 *
 * Todas as regras de negocio do desafio (antecedencia minima,
 * conflito de sala, conflito de equipamento, horario valido e
 * equipamento ativo) sao validadas em ReservaService ANTES de uma
 * instancia desta entidade ser persistida.
 */
@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    /**
     * Data/hora em que os equipamentos serao retirados.
     */
    @Column(nullable = false)
    private LocalDateTime horarioRetirada;

    /**
     * Data/hora em que os equipamentos deverao ser devolvidos.
     */
    @Column(nullable = false)
    private LocalDateTime horarioEntrega;

    /**
     * Momento em que a reserva foi criada no sistema (auditoria).
     */
    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    /**
     * Equipamentos incluidos nesta reserva. Uma reserva pode conter
     * varios equipamentos, e um equipamento pode fazer parte de varias
     * reservas ao longo do tempo (em periodos diferentes).
     */
    @ManyToMany
    @JoinTable(
            name = "reserva_equipamentos",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "equipamento_id")
    )
    @Builder.Default
    private List<Equipamento> equipamentos = new ArrayList<>();
}
