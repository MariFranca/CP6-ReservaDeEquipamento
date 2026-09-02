package br.com.fiap.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa o professor que solicita a reserva de equipamentos.
 */
@Entity
@Table(name = "professores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    private String email;
}
