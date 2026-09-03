# Sistema de Reserva de Equipamentos

API REST desenvolvida em **Spring Boot** para controlar a reserva de
equipamentos (datashows, microfones, cabos, extensões etc.) por professores,
validando automaticamente conflitos de sala, de equipamento e prazos mínimos
de antecedência.

> Evolução da API de "Produtos" feita em aula: o cadastro de **Produto** deu
> lugar ao cadastro de **Equipamento**, e a aplicação passou a controlar
> **reservas** feitas por professores, aplicando as regras de negócio do
> desafio proposto.

---

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 17 | Linguagem |
| Spring Boot 3.3 (Web, Data JPA, Validation) | Framework principal |
| Lombok | Redução de boilerplate (getters/setters/builders) |
| H2 Database | Banco em memória (perfil padrão) |
| Oracle (`ojdbc11`) | Banco alternativo (perfil `oracle`) |
| JUnit 5 + Mockito | Testes unitários |
| Insomnia / Postman | Testes manuais da API |

---

## ▶️ Como executar

```bash
./mvnw spring-boot:run
```

A aplicação sobe em **`http://localhost:8080`**.

O H2 já é populado automaticamente ao iniciar (`data.sql`) com:
- 2 professores
- 2 cursos
- 2 salas
- 5 equipamentos (incluindo `Microfone 01`, propositalmente **inativo**, para testar a regra de equipamento inativo)

**Console H2:** `http://localhost:8080/h2-console`
JDBC URL: `jdbc:h2:mem:reservas` · usuário: `sa` · senha: *(em branco)*

### Usando Oracle em vez do H2

Edite `src/main/resources/application-oracle.properties` com os dados do seu
banco e rode:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=oracle
```

---

## Modelo de domínio

| Entidade | Tabela | Descrição |
|---|---|---|
| `Professor` | `professores` | Quem solicita a reserva. Campos: `id`, `nome`, `email` (único). |
| `Curso` | `cursos` | Curso vinculado à reserva. Campos: `id`, `nome`. |
| `Sala` | `salas` | Sala onde os equipamentos serão usados. Campos: `id`, `numero` (único), `bloco`. |
| `Equipamento` | `equipamentos` | Evolução da antiga entidade `Produto`. Cada registro é uma **unidade física** (ex.: "Datashow 01"), não apenas um tipo genérico. Campos: `id`, `identificacao` (única), `tipo`, `descricao`, `ativo`. |
| `Reserva` | `reservas` | Liga professor + curso + sala + período (`horarioRetirada` → `horarioEntrega`) a uma lista de equipamentos (`reserva_equipamentos`, N:N). Também guarda `dataCriacao` para auditoria. |

---

## Endpoints da API

Todos os endpoints recebem e retornam JSON. Em caso de erro, o formato de
resposta é padronizado (veja [Tratamento de erros](#️-tratamento-de-erros)).

### Professores — `/professores`

| Método | Endpoint | Descrição | Corpo (body) |
|---|---|---|---|
| `GET` | `/professores` | Lista todos os professores | – |
| `GET` | `/professores/{id}` | Busca um professor pelo id (404 se não existir) | – |
| `POST` | `/professores` | Cadastra um novo professor | `{ "nome": "string", "email": "string" }` |
| `DELETE` | `/professores/{id}` | Exclui um professor | – |

### Cursos — `/cursos`

| Método | Endpoint | Descrição | Corpo (body) |
|---|---|---|---|
| `GET` | `/cursos` | Lista todos os cursos | – |
| `GET` | `/cursos/{id}` | Busca um curso pelo id (404 se não existir) | – |
| `POST` | `/cursos` | Cadastra um novo curso | `{ "nome": "string" }` |
| `DELETE` | `/cursos/{id}` | Exclui um curso | – |

### Salas — `/salas`

| Método | Endpoint | Descrição | Corpo (body) |
|---|---|---|---|
| `GET` | `/salas` | Lista todas as salas | – |
| `GET` | `/salas/{id}` | Busca uma sala pelo id (404 se não existir) | – |
| `POST` | `/salas` | Cadastra uma nova sala | `{ "numero": "string", "bloco": "string" }` |
| `DELETE` | `/salas/{id}` | Exclui uma sala | – |

### Equipamentos — `/equipamentos`

| Método | Endpoint | Descrição | Corpo (body) |
|---|---|---|---|
| `GET` | `/equipamentos` | Lista **todos** os equipamentos (ativos e inativos) | – |
| `GET` | `/equipamentos/ativos` | Lista somente os equipamentos com `ativo = true` | – |
| `GET` | `/equipamentos/{id}` | Busca um equipamento pelo id (404 se não existir) | – |
| `POST` | `/equipamentos` | Cadastra um novo equipamento | `{ "identificacao": "string", "tipo": "string", "descricao": "string", "ativo": true }` |
| `PUT` | `/equipamentos/{id}` | Atualiza um equipamento existente (ex.: para desativá-lo) | `{ "identificacao": "string", "tipo": "string", "descricao": "string", "ativo": false }` |
| `DELETE` | `/equipamentos/{id}` | Exclui um equipamento | – |

### Reservas — `/reservas`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/reservas` | Lista todas as reservas |
| `GET` | `/reservas/{id}` | Busca uma reserva pelo id (404 se não existir) |
| `POST` | `/reservas` | Cria uma nova reserva, validando todas as regras de negócio |
| `DELETE` | `/reservas/{id}` | Cancela (exclui) uma reserva |

#### `POST /reservas` — corpo da requisição

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

| Campo | Tipo | Obrigatório | Observação |
|---|---|---|---|
| `professorId` | `Long` | ✅ | Deve existir na base |
| `cursoId` | `Long` | ✅ | Deve existir na base |
| `salaId` | `Long` | ✅ | Deve existir na base |
| `horarioRetirada` | `LocalDateTime` (ISO-8601) | ✅ | Início do período de uso |
| `horarioEntrega` | `LocalDateTime` (ISO-8601) | ✅ | Fim do período de uso |
| `equipamentoIds` | `List<Long>` | ✅ | Ao menos 1 id; ids duplicados são ignorados |

#### Resposta (`200 OK`)

```json
{
  "id": 1,
  "professor": "João da Silva",
  "curso": "Engenharia de Software",
  "sala": "204",
  "horarioRetirada": "2026-09-20T18:30:00",
  "horarioEntrega": "2026-09-20T22:30:00",
  "equipamentos": ["Datashow 01", "Cabo HDMI 01", "Extensão 01"]
}
```

Um `ReservaResponseDTO` é usado na resposta para não expor as entidades JPA
diretamente (evita referências cíclicas no JSON e vazamento de detalhes
internos de mapeamento).

---

## Regras de negócio (`ReservaService`)

Ao criar uma reserva (`POST /reservas`), as regras abaixo são validadas **em
ordem**, antes de qualquer gravação no banco. Se qualquer uma delas for
violada, a API responde `400 Bad Request` com a mensagem explicando o motivo.

| # | Regra | Descrição |
|---|---|---|
| 1 | **Existência das referências** | Professor, curso, sala e todos os equipamentos informados precisam existir. Caso contrário → `404 Not Found`. |
| 2 | **Horário válido** | `horarioRetirada` deve ser **estritamente anterior** a `horarioEntrega` (retirada = entrega também é rejeitado). |
| 3 | **Antecedência mínima de 7 dias** | A reserva precisa ser feita com pelo menos **7 dias** de antecedência da data de retirada, contados a partir de hoje. |
| 4 | **Equipamento ativo** | Equipamentos com `ativo = false` não podem ser reservados. |
| 5 | **Conflito de sala** | A sala não pode ter outra reserva com período sobreposto ao solicitado. |
| 6 | **Conflito de equipamento** | Nenhum dos equipamentos da reserva pode já estar comprometido em outra reserva com período sobreposto. |

### Fórmula de sobreposição de período

A verificação de conflito (sala e equipamento) usa a fórmula clássica de
intervalos, feita diretamente via JPQL no banco (`ReservaRepository`):

```
Dois períodos [a, b) e [c, d) se sobrepõem quando:  a < d  E  c < b
```

Ou seja, uma reserva existente conflita com a nova quando:

```
retiradaExistente < entregaNova   E   entregaExistente > retiradaNova
```

### Exemplo de erro (`400 Bad Request`)

```json
{
  "timestamp": "2026-09-01T10:00:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "A reserva deve ser feita com no mínimo 7 dias de antecedência. Data informada: 2026-09-05 (faltam apenas 4 dia(s) de antecedência)."
}
```

Outros exemplos de mensagens de erro geradas pelas regras:

- `"O horário de retirada deve ser anterior ao horário de entrega."`
- `"Os seguintes equipamentos estão inativos e não podem ser reservados: Microfone 01"`
- `"A sala 204 já possui uma reserva no período de 2026-09-20T18:30 até 2026-09-20T22:30."`
- `"O equipamento 'Datashow 01' já está reservado no período de 2026-09-20T18:30 até 2026-09-20T22:30."`

---

## Tratamento de erros

Todas as exceções de negócio e de validação são centralizadas em
`ApiExceptionHandler` (`@RestControllerAdvice`), garantindo um formato de
resposta padronizado e consistente em toda a API:

| Situação | Status HTTP | Exceção |
|---|---|---|
| Regra de negócio violada (horário, antecedência, conflito, equipamento inativo) | `400 Bad Request` | `ReservaInvalidaException` |
| Campo inválido no corpo da requisição (`@Valid`) | `400 Bad Request` | `MethodArgumentNotValidException` |
| Professor, curso, sala, equipamento ou reserva não encontrados | `404 Not Found` | `RecursoNaoEncontradoException` |

Formato padrão do corpo de erro:

```json
{
  "timestamp": "2026-09-01T10:00:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "descrição clara do motivo do erro"
}
```

---

## Testes

```bash
./mvnw test
```

`ReservaServiceTest` cobre, com Mockito, cada uma das regras de negócio
individualmente:

- Professor / curso / sala / equipamento inexistente
- Horário inválido (retirada não anterior à entrega)
- Antecedência insuficiente (< 7 dias)
- Equipamento inativo
- Conflito de sala
- Conflito de equipamento
- Caminho feliz (reserva criada com sucesso)

---

## Estrutura do projeto

```
src/main/java/br/com/fiap/reservas/
├── entity/       Professor, Curso, Sala, Equipamento, Reserva
├── dto/          ReservaRequestDTO, ReservaResponseDTO
├── repository/   Spring Data JPA + queries JPQL de conflito de horário
├── service/      Regras de negócio (destaque para ReservaService)
├── controller/   Endpoints REST
└── exception/    Exceções de negócio + handler global (@RestControllerAdvice)
```

---

## Principais melhorias em relação à API de Produtos original

1. **Modelagem de domínio completa** (`Professor`, `Curso`, `Sala`,
   `Equipamento`, `Reserva`) em vez de uma única entidade genérica.
2. **Motor de validação de regras de negócio centralizado** em
   `ReservaService`, com mensagens de erro claras e tratadas globalmente via
   `@RestControllerAdvice`.
3. **Consultas de conflito de horário** (sala e equipamento) feitas
   diretamente no banco via JPQL, evitando checar sobreposição de datas "na
   mão" em memória.
4. **DTOs de entrada e saída** (`ReservaRequestDTO` / `ReservaResponseDTO`)
   para não expor as entidades JPA diretamente na API.