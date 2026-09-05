# API de Matrículas

API REST de matrículas acadêmicas: alunos, turmas e a matrícula que liga os dois.

Projeto de estudo de Spring Boot. O módulo `aluno` é referência escrita pelo
Claude; `turma` e `matricula` são escritos pelo dono do repo, com os testes
vindo antes da implementação. As regras estão em [`CLAUDE.md`](CLAUDE.md).

## Stack

| | |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Persistência | Spring Data JPA + Hibernate 7 |
| Banco | H2 em memória |
| Documentação | springdoc-openapi 3.1.0 |
| Build | Maven (wrapper incluso) |

## Rodando

```bash
./mvnw spring-boot:run
```

| O quê | Onde |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Console do H2 | http://localhost:8080/h2-console |

Credenciais do H2: JDBC URL `jdbc:h2:mem:matriculas`, usuário `sa`, senha vazia.

## Testes

```bash
./mvnw test
```

## Notas de configuração

**`ddl-auto=create-drop`.** O schema é recriado a cada subida e derrubado no
fechamento do contexto. Com H2 em memória o efeito na aplicação é quase o do
`create`; a diferença aparece nos testes, onde vários contextos Spring dividem a
mesma JVM e isso evita um teste sujar o outro. Consequência: nenhum dado
sobrevive a um restart.

**`open-in-view=false`.** Desligado de propósito. Com ele ligado a sessão JPA
fica aberta até o fim do request, então um `@ManyToOne(fetch = LAZY)` carrega
silenciosamente durante a serialização e um N+1 passa despercebido. Desligado, o
acesso fora da transação estoura `LazyInitializationException` e o problema
aparece no teste, que é onde deve aparecer.

## Modelo

`Matricula` é entidade própria, com `@ManyToOne` para `Aluno` e para `Turma`.
Não existe `@ManyToMany` no projeto: a matrícula tem dados próprios (data e
status) e uma tabela de junção não teria onde guardá-los.

## Estrutura

```
br/com/matriculas/
  erro/          tratamento de erro global (@RestControllerAdvice)
  aluno/         módulo de referência
  turma/
  matricula/
  config/
docs/PADRAO.md   checklist extraído do módulo aluno
docs/reviews/    listas de review, uma por módulo
```

Pacote por feature, não por camada.
