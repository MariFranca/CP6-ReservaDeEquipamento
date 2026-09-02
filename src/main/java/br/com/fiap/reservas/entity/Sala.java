package br.com.fiap.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Sala de aula/laboratorio onde o equipamento sera utilizado.
 * Uma sala nao pode possuir duas reservas com horarios conflitantes.
 */
@Entity
@Table(name = "salas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    private String bloco;
}
