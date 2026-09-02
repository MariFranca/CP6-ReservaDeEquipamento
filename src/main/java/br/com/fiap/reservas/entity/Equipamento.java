package br.com.fiap.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ============================================================
 * ENTIDADE EQUIPAMENTO
 * ============================================================
 *
 * Evolucao da antiga entidade "Produto" da API desenvolvida em aula.
 *
 * Cada registro representa uma UNIDADE fisica de equipamento que pode
 * ser reservada (ex.: "Datashow 01", "Datashow 02", "Microfone 01"),
 * e nao apenas um "tipo" de produto. Isso permite controlar a
 * disponibilidade de cada unidade individualmente, como pedido no
 * enunciado do desafio.
 *
 *     Classe Java              Banco de dados
 *     ------------------------------------------------
 *     Equipamento      <---->  tabela equipamentos
 *     id               <---->  coluna id
 *     identificacao    <---->  coluna identificacao   (ex: "Datashow 01")
 *     tipo             <---->  coluna tipo             (ex: "Datashow")
 *     descricao        <---->  coluna descricao
 *     ativo            <---->  coluna ativo
 */
@Entity
@Table(name = "equipamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificacao unica do equipamento, ex: "Datashow 01".
     */
    @Column(nullable = false, unique = true)
    private String identificacao;

    /**
     * Tipo/categoria do equipamento, ex: "Datashow", "Microfone",
     * "Extensao", "Cabo HDMI", "Computador", "Caixa de som", "Adaptador".
     */
    @Column(nullable = false)
    private String tipo;

    private String descricao;

    /**
     * Somente equipamentos ativos podem ser reservados (regra de negocio 5).
     */
    @Column(nullable = false)
    private Boolean ativo;
}
