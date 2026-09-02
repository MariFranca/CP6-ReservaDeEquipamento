package br.com.fiap.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sistema de Reserva de Equipamentos.
 *
 * Evolucao da API de Produtos desenvolvida em aula: o antigo cadastro de
 * "Produto" deu lugar ao cadastro de "Equipamento", e o sistema passou a
 * controlar reservas de equipamentos feitas por professores, validando
 * conflitos de sala, horario e disponibilidade dos equipamentos.
 */
@SpringBootApplication
public class ReservaEquipamentosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservaEquipamentosApplication.class, args);
    }
}
