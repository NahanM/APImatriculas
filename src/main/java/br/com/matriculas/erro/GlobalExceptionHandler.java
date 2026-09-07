package br.com.matriculas.erro;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Tradutor único de exceção para resposta HTTP. Nenhum controller do projeto
 * escreve try/catch: quem quebra uma regra lança, e este arquivo decide o status.
 *
 * <p>PADRÃO: estende {@link ResponseEntityExceptionHandler}. A classe base traz
 * 21 handlers prontos para as exceções do próprio Spring MVC (JSON malformado,
 * verbo não suportado, content-type errado, parâmetro obrigatório ausente).
 * Sem estender, um POST com JSON quebrado responderia 500 com stack trace em vez
 * de 400. Herdando, ganhamos tudo isso e sobrescrevemos só o que queremos mudar.
 *
 * <p>PADRÃO: o corpo de erro é sempre {@link ProblemDetail} (RFC 9457), nunca um
 * record próprio. Sai com {@code Content-Type: application/problem+json} e o
 * springdoc documenta o schema sozinho.
 *
 * <p>PADRÃO: devolver {@code ProblemDetail} já define o status da resposta a
 * partir do status de dentro do objeto. Não precisa de {@code @ResponseStatus}
 * nem de {@code ResponseEntity} por fora.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 404: o id veio na requisição mas não existe no banco.
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    ProblemDetail recursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Recurso não encontrado");
        return problema;
    }

    /**
     * 409: a requisição faz sentido, mas o estado atual do sistema não permite.
     *
     * <p>Este handler casa com qualquer subclasse de
     * {@link RegraDeNegocioException}. Quando aparecerem
     * {@code MatriculaDuplicadaException} e {@code TurmaLotadaException}, elas
     * caem aqui sem precisar de handler novo.
     */
    @ExceptionHandler(RegraDeNegocioException.class)
    ProblemDetail regraDeNegocio(RegraDeNegocioException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problema.setTitle("Regra de negócio violada");
        return problema;
    }

    // ------------------------------------------------------------------
    // TODO (seu): 400 com a lista de campos que falharam na validação.
    //
    // A classe base JÁ devolve 400 aqui, com detail genérico "Invalid request
    // content." e nenhuma informação de qual campo quebrou. Rode o teste antes
    // de escrever nada para ver esse comportamento. Seu trabalho não é criar o
    // 400, é enriquecer o corpo dele.
    //
    // Sobrescreva (assinatura exata, conferida no jar do spring-webmvc 7.0.9):
    //
    //   @Override
    //   protected ResponseEntity<Object> handleMethodArgumentNotValid(
    //           MethodArgumentNotValidException ex,
    //           HttpHeaders headers,
    //           HttpStatusCode status,
    //           WebRequest request)
    //
    // Pontos da doc, sem a solução:
    //   - ex.getBindingResult().getFieldErrors() devolve os campos que falharam;
    //     cada FieldError tem getField() e getDefaultMessage().
    //   - ProblemDetail.setProperty(String, Object) adiciona um campo extra ao
    //     corpo, fora dos cinco do RFC. É onde a lista de campos deve ir.
    //   - Para pegar o ProblemDetail que a base já montou: ex.getBody().
    //   - Devolva com ResponseEntity, porque a assinatura da base exige.
    // ------------------------------------------------------------------

    // ------------------------------------------------------------------
    // TODO (seu): 409 para DataIntegrityViolationException.
    //
    // É a exceção que o Spring Data lança quando uma constraint do banco
    // estoura (o unique de email do Aluno, o unique de (aluno, turma) da
    // Matrícula). É a rede de segurança para o caso em que a checagem no
    // service perdeu uma corrida entre duas requisições simultâneas.
    //
    // Mesma forma dos dois handlers acima: @ExceptionHandler + ProblemDetail.
    //
    // Cuidado: a mensagem original vem do Hibernate e vaza nome de tabela e de
    // constraint para o cliente. Escreva um detail seu; não repasse
    // ex.getMessage().
    // ------------------------------------------------------------------
}
