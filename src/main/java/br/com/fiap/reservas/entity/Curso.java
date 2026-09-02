package br.com.fiap.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Curso academico ao qual a aula/reserva esta vinculada.
 * Ex.: Engenharia de Software, Analise e Desenvolvimento de Sistemas.
 */
@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;
}
