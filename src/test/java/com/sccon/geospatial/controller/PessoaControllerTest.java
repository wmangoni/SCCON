package com.sccon.geospatial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sccon.geospatial.model.Pessoa;
import com.sccon.geospatial.service.PessoaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PessoaController.class)
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PessoaService pessoaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pessoa pessoaTeste;

    @BeforeEach
    void setUp() {
        pessoaTeste = new Pessoa(1L, "José da Silva", 
            LocalDate.of(2000, 4, 6), 
            LocalDate.of(2020, 5, 10));
        
    }

    @Test
    void listarPessoas_DeveRetornarListaOrdenadaPorNome() throws Exception {
        List<Pessoa> pessoasOrdenadas = Arrays.asList(
            new Pessoa(3L, "João Oliveira", LocalDate.of(1988, 12, 3), LocalDate.of(2021, 1, 15)),
            new Pessoa(1L, "José da Silva", LocalDate.of(2000, 4, 6), LocalDate.of(2020, 5, 10)),
            new Pessoa(2L, "Maria Santos", LocalDate.of(1995, 8, 15), LocalDate.of(2019, 3, 20))
        );
        
        when(pessoaService.listarPessoas()).thenReturn(pessoasOrdenadas);

        mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].nome").value("João Oliveira"))
                .andExpect(jsonPath("$[1].nome").value("José da Silva"))
                .andExpect(jsonPath("$[2].nome").value("Maria Santos"));

        verify(pessoaService, times(1)).listarPessoas();
    }

    @Test
    void buscarPorId_QuandoPessoaExiste_DeveRetornarPessoa() throws Exception {
        when(pessoaService.buscarPorId(1L)).thenReturn(pessoaTeste);

        mockMvc.perform(get("/person/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("José da Silva"))
                .andExpect(jsonPath("$.dataNascimento").value("2000-04-06"))
                .andExpect(jsonPath("$.dataAdmissao").value("2020-05-10"));

        verify(pessoaService, times(1)).buscarPorId(1L);
    }

    @Test
    void buscarPorId_QuandoPessoaNaoExiste_DeveRetornar404() throws Exception {
        when(pessoaService.buscarPorId(999L))
                .thenThrow(new com.sccon.geospatial.exception.PessoaNotFoundException("Pessoa com ID 999 não encontrada"));

        mockMvc.perform(get("/person/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Pessoa com ID 999 não encontrada"));

        verify(pessoaService, times(1)).buscarPorId(999L);
    }

    @Test
    void criarPessoa_ComIdAutomatico_DeveRetornar201() throws Exception {
        Pessoa novaPessoa = new Pessoa(null, "Ana Costa", 
            LocalDate.of(1992, 3, 15), 
            LocalDate.of(2022, 6, 1));
        
        Pessoa pessoaCriada = new Pessoa(4L, "Ana Costa", 
            LocalDate.of(1992, 3, 15), 
            LocalDate.of(2022, 6, 1));

        when(pessoaService.criarPessoa(any(Pessoa.class))).thenReturn(pessoaCriada);

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaPessoa)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.nome").value("Ana Costa"))
                .andExpect(jsonPath("$.dataNascimento").value("1992-03-15"))
                .andExpect(jsonPath("$.dataAdmissao").value("2022-06-01"));

        verify(pessoaService, times(1)).criarPessoa(any(Pessoa.class));
    }

    @Test
    void criarPessoa_ComIdEspecifico_DeveRetornar201() throws Exception {
        Pessoa novaPessoa = new Pessoa(5L, "Pedro Silva", 
            LocalDate.of(1985, 7, 20), 
            LocalDate.of(2023, 2, 10));

        when(pessoaService.criarPessoa(any(Pessoa.class))).thenReturn(novaPessoa);

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaPessoa)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nome").value("Pedro Silva"));

        verify(pessoaService, times(1)).criarPessoa(any(Pessoa.class));
    }

    @Test
    void criarPessoa_ComIdExistente_DeveRetornar409() throws Exception {
        Pessoa pessoaConflito = new Pessoa(1L, "Teste Conflito", 
            LocalDate.of(1990, 1, 1), 
            LocalDate.of(2020, 1, 1));

        when(pessoaService.criarPessoa(any(Pessoa.class)))
                .thenThrow(new com.sccon.geospatial.exception.PessoaConflictException("Pessoa com ID 1 já existe"));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaConflito)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Pessoa com ID 1 já existe"));

        verify(pessoaService, times(1)).criarPessoa(any(Pessoa.class));
    }

    @Test
    void criarPessoa_ComDadosInvalidos_DeveRetornar400() throws Exception {
        Pessoa pessoaInvalida = new Pessoa(null, "", 
            LocalDate.of(1990, 1, 1), 
            LocalDate.of(2020, 1, 1));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Nome não pode ser vazio"));

        verify(pessoaService, never()).criarPessoa(any(Pessoa.class));
    }

    @Test
    void atualizarPessoa_QuandoPessoaExiste_DeveRetornar200() throws Exception {
        Pessoa pessoaAtualizada = new Pessoa(1L, "José da Silva Atualizado", 
            LocalDate.of(2000, 4, 6), 
            LocalDate.of(2020, 5, 10));

        when(pessoaService.atualizarPessoa(eq(1L), any(Pessoa.class))).thenReturn(pessoaAtualizada);

        mockMvc.perform(put("/person/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("José da Silva Atualizado"));

        verify(pessoaService, times(1)).atualizarPessoa(eq(1L), any(Pessoa.class));
    }

    @Test
    void atualizarPessoa_QuandoPessoaNaoExiste_DeveRetornar404() throws Exception {
        Pessoa pessoaAtualizada = new Pessoa(999L, "Teste", 
            LocalDate.of(1990, 1, 1), 
            LocalDate.of(2020, 1, 1));

        when(pessoaService.atualizarPessoa(eq(999L), any(Pessoa.class)))
                .thenThrow(new com.sccon.geospatial.exception.PessoaNotFoundException("Pessoa com ID 999 não encontrada"));

        mockMvc.perform(put("/person/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaAtualizada)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pessoa com ID 999 não encontrada"));

        verify(pessoaService, times(1)).atualizarPessoa(eq(999L), any(Pessoa.class));
    }

    @Test
    void atualizarAtributo_QuandoPessoaExiste_DeveRetornar200() throws Exception {
        Pessoa pessoaAtualizada = new Pessoa(1L, "José Silva Modificado", 
            LocalDate.of(2000, 4, 6), 
            LocalDate.of(2020, 5, 10));

        when(pessoaService.atualizarParcialmente(eq(1L), any())).thenReturn(pessoaAtualizada);

        mockMvc.perform(patch("/person/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"José Silva Modificado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("José Silva Modificado"));

        verify(pessoaService, times(1)).atualizarParcialmente(eq(1L), any());
    }

    @Test
    void atualizarAtributo_ComDataNascimento_DeveRetornar200() throws Exception {
        Pessoa pessoaAtualizada = new Pessoa(1L, "José da Silva", 
            LocalDate.of(2000, 4, 7), 
            LocalDate.of(2020, 5, 10));

        when(pessoaService.atualizarParcialmente(eq(1L), any())).thenReturn(pessoaAtualizada);

        mockMvc.perform(patch("/person/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dataNascimento\":\"2000-04-07\"}"))
                .andExpect(status().isOk());

        verify(pessoaService, times(1)).atualizarParcialmente(eq(1L), any());
    }

    @Test
    void atualizarAtributo_QuandoPessoaNaoExiste_DeveRetornar404() throws Exception {
        when(pessoaService.atualizarParcialmente(eq(999L), any()))
                .thenThrow(new com.sccon.geospatial.exception.PessoaNotFoundException("Pessoa com ID 999 não encontrada"));

        mockMvc.perform(patch("/person/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Teste\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pessoa com ID 999 não encontrada"));

        verify(pessoaService, times(1)).atualizarParcialmente(eq(999L), any());
    }

    @Test
    void atualizarAtributo_ComAtributoInvalido_DeveRetornar400() throws Exception {
        when(pessoaService.atualizarParcialmente(eq(1L), any()))
                .thenThrow(new com.sccon.geospatial.exception.InvalidParameterException("Atributo 'atributoInvalido' não é válido"));

        mockMvc.perform(patch("/person/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"atributoInvalido\":\"valor\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Atributo 'atributoInvalido' não é válido"));

        verify(pessoaService, times(1)).atualizarParcialmente(eq(1L), any());
    }

    @Test
    void removerPessoa_QuandoPessoaExiste_DeveRetornar204() throws Exception {
        doNothing().when(pessoaService).removerPessoa(1L);

        mockMvc.perform(delete("/person/1"))
                .andExpect(status().isNoContent());

        verify(pessoaService, times(1)).removerPessoa(1L);
    }

    @Test
    void removerPessoa_QuandoPessoaNaoExiste_DeveRetornar404() throws Exception {
        doThrow(new com.sccon.geospatial.exception.PessoaNotFoundException("Pessoa com ID 999 não encontrada"))
                .when(pessoaService).removerPessoa(999L);

        mockMvc.perform(delete("/person/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pessoa com ID 999 não encontrada"));

        verify(pessoaService, times(1)).removerPessoa(999L);
    }

    @Test
    void calcularIdade_EmDias_DeveRetornar200() throws Exception {
        when(pessoaService.calcularIdade(1L, "days")).thenReturn(9334L);

        mockMvc.perform(get("/person/1/age?output=days"))
                .andExpect(status().isOk())
                .andExpect(content().string("9334"));

        verify(pessoaService, times(1)).calcularIdade(1L, "days");
    }

    @Test
    void calcularIdade_EmMeses_DeveRetornar200() throws Exception {
        when(pessoaService.calcularIdade(1L, "months")).thenReturn(274L);

        mockMvc.perform(get("/person/1/age?output=months"))
                .andExpect(status().isOk())
                .andExpect(content().string("274"));

        verify(pessoaService, times(1)).calcularIdade(1L, "months");
    }

    @Test
    void calcularIdade_EmAnos_DeveRetornar200() throws Exception {
        when(pessoaService.calcularIdade(1L, "years")).thenReturn(22L);

        mockMvc.perform(get("/person/1/age?output=years"))
                .andExpect(status().isOk())
                .andExpect(content().string("22"));

        verify(pessoaService, times(1)).calcularIdade(1L, "years");
    }

    @Test
    void calcularIdade_ComFormatoInvalido_DeveRetornar400() throws Exception {
        when(pessoaService.calcularIdade(1L, "invalid"))
                .thenThrow(new com.sccon.geospatial.exception.InvalidParameterException("Formato 'invalid' não é válido. Use: days, months ou years"));

        mockMvc.perform(get("/person/1/age?output=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Formato 'invalid' não é válido. Use: days, months ou years"));

        verify(pessoaService, times(1)).calcularIdade(1L, "invalid");
    }

    @Test
    void calcularIdade_QuandoPessoaNaoExiste_DeveRetornar404() throws Exception {
        when(pessoaService.calcularIdade(999L, "days"))
                .thenThrow(new com.sccon.geospatial.exception.PessoaNotFoundException("Pessoa com ID 999 não encontrada"));

        mockMvc.perform(get("/person/999/age?output=days"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pessoa com ID 999 não encontrada"));

        verify(pessoaService, times(1)).calcularIdade(999L, "days");
    }

    @Test
    void calcularSalario_EmReais_DeveRetornar200() throws Exception {
        when(pessoaService.calcularSalario(1L, "full")).thenReturn(3259.36);

        mockMvc.perform(get("/person/1/salary?output=full"))
                .andExpect(status().isOk())
                .andExpect(content().string("3259.36"));

        verify(pessoaService, times(1)).calcularSalario(1L, "full");
    }

    @Test
    void calcularSalario_EmSalariosMinimos_DeveRetornar200() throws Exception {
        when(pessoaService.calcularSalario(1L, "min")).thenReturn(2.51);

        mockMvc.perform(get("/person/1/salary?output=min"))
                .andExpect(status().isOk())
                .andExpect(content().string("2.51"));

        verify(pessoaService, times(1)).calcularSalario(1L, "min");
    }

    @Test
    void calcularSalario_ComFormatoInvalido_DeveRetornar400() throws Exception {
        when(pessoaService.calcularSalario(1L, "invalid"))
                .thenThrow(new com.sccon.geospatial.exception.InvalidParameterException("Formato 'invalid' não é válido. Use: full ou min"));

        mockMvc.perform(get("/person/1/salary?output=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Formato 'invalid' não é válido. Use: full ou min"));

        verify(pessoaService, times(1)).calcularSalario(1L, "invalid");
    }

    @Test
    void calcularSalario_QuandoPessoaNaoExiste_DeveRetornar404() throws Exception {
        when(pessoaService.calcularSalario(999L, "full"))
                .thenThrow(new com.sccon.geospatial.exception.PessoaNotFoundException("Pessoa com ID 999 não encontrada"));

        mockMvc.perform(get("/person/999/salary?output=full"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pessoa com ID 999 não encontrada"));

        verify(pessoaService, times(1)).calcularSalario(999L, "full");
    }

    @Test
    void calcularIdade_ComDataNascimentoNula_DeveRetornar400() throws Exception {
        when(pessoaService.calcularIdade(1L, "days"))
                .thenThrow(new com.sccon.geospatial.exception.InvalidParameterException("Data de nascimento não pode ser nula para calcular idade"));

        mockMvc.perform(get("/person/1/age?output=days"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Data de nascimento não pode ser nula para calcular idade"));

        verify(pessoaService, times(1)).calcularIdade(1L, "days");
    }

    @Test
    void calcularSalario_ComDataAdmissaoNula_DeveRetornar400() throws Exception {
        when(pessoaService.calcularSalario(1L, "full"))
                .thenThrow(new com.sccon.geospatial.exception.InvalidParameterException("Data de admissão não pode ser nula para calcular salário"));

        mockMvc.perform(get("/person/1/salary?output=full"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Data de admissão não pode ser nula para calcular salário"));

        verify(pessoaService, times(1)).calcularSalario(1L, "full");
    }

    @Test
    void atualizarAtributo_ComNomeVazio_DeveRetornar400() throws Exception {
        when(pessoaService.atualizarParcialmente(eq(1L), any()))
                .thenThrow(new com.sccon.geospatial.exception.InvalidParameterException("Nome não pode ser vazio"));

        mockMvc.perform(patch("/person/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Nome não pode ser vazio"));

        verify(pessoaService, times(1)).atualizarParcialmente(eq(1L), any());
    }

    @Test
    void atualizarAtributo_ComDataNascimentoNula_DeveRetornar400() throws Exception {
        when(pessoaService.atualizarParcialmente(eq(1L), any()))
                .thenThrow(new com.sccon.geospatial.exception.InvalidParameterException("Data de nascimento não pode ser nula"));

        mockMvc.perform(patch("/person/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dataNascimento\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Data de nascimento não pode ser nula"));

        verify(pessoaService, times(1)).atualizarParcialmente(eq(1L), any());
    }
}
