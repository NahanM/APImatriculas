package br.com.matriculas.erro;

/**
 * Base de toda violação de regra de negócio. Vira 409 Conflict.
 *
 * <p>Não é abstrata, então dá para lançar direto em casos simples. Mas o uso
 * principal é como pai: {@code MatriculaDuplicadaException} e
 * {@code TurmaLotadaException} vão estender esta classe e ganhar o 409 de graça,
 * sem precisar de um {@code @ExceptionHandler} novo para cada uma. O Spring
 * escolhe o handler pelo tipo mais específico que casa, e um handler de
 * {@code RegraDeNegocioException} casa com qualquer subclasse.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
