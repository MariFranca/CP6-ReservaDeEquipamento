# Sistema de Reserva de Equipamentos

Evolução da API de Produtos desenvolvida em aula: o cadastro de "Produto"
deu lugar ao cadastro de **Equipamento**, e a aplicação passou a controlar
**reservas de equipamentos** feitas por professores, validando conflitos
de sala, horário e disponibilidade — conforme o desafio proposto.

## Tecnologias

- Java 17
- Spring Boot 3.3 (Web, Data JPA, Validation)
- Lombok
- H2 (perfil padrão, em memória) e Oracle (perfil `oracle`, opcional)
- JUnit 5 + Mockito
- Insomnia para testes manuais da API

## Como executar

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. O H2 já é populado com dados
de exemplo (`data.sql`): 2 professores, 2 cursos, 2 salas e 5 equipamentos
(incluindo um `Microfone 01` propositalmente **inativo**, para testar a
regra 5).

Console H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:reservas`, usuário `sa`, sem senha).

Para usar Oracle em vez do H2, edite `src/main/resources/application-oracle.properties`
com os dados do seu banco e rode com `-Dspring.profiles.active=oracle`.

## Modelo de domínio

| Entidade      | Descrição                                                             |
|---------------|------------------------------------------------------------------------|
| `Professor`   | Quem solicita a reserva                                               |
| `Curso`       | Curso vinculado à reserva                                              |
| `Sala`        | Sala onde os equipamentos serão utilizados                             |
| `Equipamento` | Evolução da antiga entidade `Produto`. Cada registro é uma **unidade** física (ex.: "Datashow 01"), com um `tipo`, e um flag `ativo` |
| `Reserva`     | Liga professor + curso + sala + período (`horarioRetirada`/`horarioEntrega`) a uma lista de equipamentos |

## Endpoints principais

Cadastros de apoio (CRUD simples): `/professores`, `/cursos`, `/salas`, `/equipamentos`.

### `POST /reservas` — criar uma reserva

```json
{
  "professorId": 1,
  "cursoId": 1,
  "salaId": 1,
  "horarioRetirada": "2026-09-20T18:30:00",
  "horarioEntrega": "2026-09-20T22:30:00",
  "equipamentoIds": [1, 4, 5]
}
```

Se alguma regra de negócio for violada, a API responde `400 Bad Request`
com uma mensagem clara do motivo, por exemplo:

```json
{
  "timestamp": "2026-09-01T10:00:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "A reserva deve ser feita com no mínimo 7 dias de antecedência. Data informada: 2026-09-05 (faltam apenas 4 dia(s) de antecedência)."
}
```

Outros endpoints: `GET /reservas`, `GET /reservas/{id}`, `DELETE /reservas/{id}` (cancela a reserva).

## Regras de negócio implementadas (`ReservaService`)

Todas as regras abaixo são validadas, em ordem, antes de qualquer reserva
ser gravada:

1. **Horário válido** — retirada estritamente anterior à entrega (retirada = entrega também é rejeitado).
2. **Antecedência mínima** — a reserva precisa ser feita com pelo menos 7 dias de antecedência da data de retirada.
3. **Equipamento ativo** — equipamentos inativos não podem ser reservados.
4. **Conflito de sala** — a sala não pode ter outra reserva com período sobreposto.
5. **Conflito de equipamento** — nenhum dos equipamentos da reserva pode estar comprometido em outra reserva com período sobreposto.

A verificação de sobreposição de período usa a fórmula clássica de
intervalos: dois períodos `[a, b)` e `[c, d)` se sobrepõem quando
`a < d E c < b`.

## Testes

```bash
./mvnw test
```

`ReservaServiceTest` cobre, com Mockito, cada uma das regras acima
individualmente (professor/curso/sala/equipamento inexistente, horário
inválido, antecedência insuficiente, equipamento inativo, conflito de
sala e conflito de equipamento) e o caminho feliz (reserva criada com
sucesso).

## Estrutura do projeto

```
src/main/java/br/com/fiap/reservas/
├── entity/       Professor, Curso, Sala, Equipamento, Reserva
├── dto/          ReservaRequestDTO, ReservaResponseDTO
├── repository/   Spring Data JPA + queries de conflito de horário
├── service/      Regras de negócio (destaque para ReservaService)
├── controller/   Endpoints REST
└── exception/    Exceções de negócio + handler global (respostas de erro padronizadas)
```

## Principais melhorias em relação à API de Produtos original

1. Modelagem de domínio completa (Professor, Curso, Sala, Equipamento, Reserva) em vez de uma única entidade.
2. Motor de validação de regras de negócio centralizado em `ReservaService`, com mensagens de erro claras e tratadas globalmente via `@RestControllerAdvice`.
3. Consultas de conflito de horário (sala e equipamento) feitas diretamente no banco via JPQL, evitando checar sobreposição de datas "na mão" em memória.
