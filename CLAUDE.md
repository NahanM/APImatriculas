# Projeto: API de Matrículas (Spring Boot)

## Como trabalhar comigo
- Antes de escrever código, explique em até 3 linhas o que vai fazer e espere eu confirmar.
- Uma feature por vez. Não adiante próximos passos.
- Ao terminar uma feature, me faça 1 pergunta, no ponto em que a decisão ainda
  estava aberta. Pergunta depois do código pronto testa memória; pergunta sobre
  a decisão testa entendimento.
- Quando eu travar, me dê o erro reduzido e o ponto da doc, não a solução. Só
  escreva o código se eu pedir com as palavras "me dá o código".
- Se eu pedir algo que quebra uma regra abaixo, avise em vez de obedecer.

## Divisão de trabalho
- Módulo `aluno` é seu, completo. Ele é o padrão de referência que eu vou copiar.
- Módulos `turma` e `matricula` são meus, completos: entidade, repository, DTO,
  mapper, service e controller. Você não escreve esses arquivos.
- Fundação, tratamento de erro, OpenAPI e README são seus.
- Testes são sempre seus, inclusive dos meus módulos, e vêm antes: você commita
  o teste falhando e eu faço passar.
- Nos meus módulos você só cria o arquivo vazio com a assinatura e um TODO
  descrevendo a regra. Nada de corpo de método. Em método com retorno não-void,
  o placeholder é `throw new UnsupportedOperationException("TODO: ...")`.
- Exceção para repositories dos meus módulos: crie só a interface com
  `extends JpaRepository` e um TODO em comentário. Não declare os métodos
  derivados. Em Spring Data a assinatura já é a implementação, e declarar por
  mim me tira o exercício.

## Como revisar meus módulos
- O padrão vive em `docs/PADRAO.md`, extraído do módulo `aluno`. Ele existe
  antes de eu escrever, não depois. Regra que não está lá não vale no review.
- Review em duas passadas, eu primeiro: eu passo o `docs/PADRAO.md` sozinho e
  escrevo minha lista em `docs/reviews/<modulo>-meu.md`. Só depois você escreve
  a sua em `docs/reviews/<modulo>-seu.md` e comparamos.
- O que você achou e eu não achei é a lista do que eu ainda não enxergo. É esse
  o número que interessa, não a contagem de erros.
- Toda diferença que você apontar vem com o motivo do padrão ser daquele jeito.
  Sem motivo, não é erro, é preferência sua, e sai do checklist.

## Regras técnicas
- Java 21, Maven, Spring Boot 4.x, JPA, H2 em memória.
- springdoc-openapi 3.x (a v2 não funciona no Boot 4).
- Matrícula é entidade própria com @ManyToOne para Aluno e Turma. Não usar @ManyToMany.
- Validação de entrada com Bean Validation nos DTOs, nunca nas entidades.
- Erros retornam via @RestControllerAdvice.
- Se você for sugerir algo que só existia no Spring Boot 3, avise antes que é padrão antigo.

## Armadilhas do Boot 4 já verificadas
- `@MockBean` não existe mais. Use `@MockitoBean`, de
  `org.springframework.test.context.bean.override.mockito` (vem do `spring-test`).
- O import do `@DataJpaTest` mudou: agora é
  `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`.
- `@DataJpaTest` não registra `@Service`. Teste de service precisa de
  `@Import(XService.class)`.
- `@DataJpaTest` é `@Transactional` com rollback. Teste de constraint única
  precisa de `saveAndFlush()`, senão a violação nunca dispara.
