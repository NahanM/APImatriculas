package br.com.matriculas.erro;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PADRÃO: teste de camada web usa {@code @WebMvcTest}, que sobe só o MVC. Sem
 * banco, sem service, sem servidor de verdade.
 *
 * <p>Repare no que NÃO está aqui: nenhum {@code @Import(GlobalExceptionHandler.class)}.
 * O {@code WebMvcTypeExcludeFilter} do Boot 4 tem {@code ControllerAdvice} na
 * lista de tipos que entram no scan, então o advice é encontrado sozinho. Já o
 * {@link ControllerDeTeste} é classe aninhada aqui dentro, fora do alcance do
 * component scan da aplicação, e por isso precisa do {@code @Import}.
 *
 * <p>Import do Boot 4: {@code org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest}.
 * Todo tutorial na internet vai mostrar o caminho do Boot 3
 * ({@code ...boot.test.autoconfigure.web.servlet}), que não existe mais.
 */
@WebMvcTest
@Import(GlobalExceptionHandlerTest.ControllerDeTeste.class)
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("RecursoNaoEncontradoException vira 404 com corpo problem+json")
    void recursoNaoEncontradoVira404() throws Exception {
        mvc.perform(get("/teste-de-erro/nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Aluno 99 não encontrado"));
    }

    @Test
    @DisplayName("RegraDeNegocioException vira 409")
    void regraDeNegocioVira409() throws Exception {
        mvc.perform(get("/teste-de-erro/regra"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"))
                .andExpect(jsonPath("$.detail").value("Turma sem vagas"));
    }

    @Test
    @DisplayName("JSON malformado vira 400, herdado da classe base sem escrever handler")
    void jsonMalformadoVira400() throws Exception {
        mvc.perform(post("/teste-de-erro/validacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json }"))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------
    // TODO (seu): body inválido vira 400 COM a lista de campos que falharam.
    //
    // Rode este teste antes de escrever o handler. O endpoint /validacao já
    // existe abaixo e já tem @Valid, então a exceção já é lançada e a classe
    // base já responde 400. O que falta é o corpo dizer QUAIS campos quebraram.
    //
    // POST /teste-de-erro/validacao com {"nome":"","email":"nao-e-email"}
    // deve dar 400 e o corpo deve listar os dois campos.
    //
    // Você escolhe o formato da lista, e o teste é onde você fixa essa escolha.
    // Uma vez fixada, ela vale para a API inteira, então pense em como o
    // front-end vai consumir isso antes de decidir.
    // ------------------------------------------------------------------

    // ------------------------------------------------------------------
    // TODO (seu): DataIntegrityViolationException vira 409.
    //
    // Adicione um endpoint no ControllerDeTeste que lance essa exceção e um
    // teste que prove o 409. Prove também que a mensagem do Hibernate não
    // vazou: o detail não pode conter o nome da constraint.
    // ------------------------------------------------------------------

    /**
     * Controller que só existe para o advice ter o que traduzir. Fica em
     * {@code src/test} de propósito: não polui a aplicação de verdade.
     */
    @RestController
    @RequestMapping("/teste-de-erro")
    static class ControllerDeTeste {

        @GetMapping("/nao-encontrado")
        void naoEncontrado() {
            throw RecursoNaoEncontradoException.de("Aluno", 99);
        }

        @GetMapping("/regra")
        void regra() {
            throw new RegraDeNegocioException("Turma sem vagas");
        }

        /**
         * O {@code @Valid} é o que faz o Spring lançar
         * {@code MethodArgumentNotValidException}. Sem ele, o body inválido
         * entraria no método sem reclamação nenhuma. A validação mora no DTO
         * (regra do CLAUDE.md), e o {@code @Valid} é o gatilho dela.
         */
        @PostMapping("/validacao")
        void validacao(@RequestBody @Valid EntradaDeTeste entrada) {
            // corpo vazio de propósito: o teste só liga para o que acontece antes
        }
    }

    record EntradaDeTeste(@NotBlank String nome, @Email String email) {
    }
}
