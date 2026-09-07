package br.com.matriculas.erro;

/**
 * Lançada quando um id existe na requisição mas não existe no banco.
 *
 * <p>Estende {@link RuntimeException} e não {@link RegraDeNegocioException} de
 * propósito: "não achei" e "achei mas não pode" são situações diferentes e viram
 * status HTTP diferentes (404 e 409). Deixá-las em hierarquias separadas faz o
 * mapeamento no {@link GlobalExceptionHandler} ser 1 para 1, sem depender da
 * ordem em que o Spring escolhe o handler mais específico.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    /**
     * Atalho para o caso mais comum: "Aluno 42 não encontrado".
     */
    public static RecursoNaoEncontradoException de(String recurso, Object id) {
        return new RecursoNaoEncontradoException(recurso + " " + id + " não encontrado");
    }
}
