package com.sccon.geospatial.service;

import com.sccon.geospatial.exception.InvalidParameterException;
import com.sccon.geospatial.exception.PessoaConflictException;
import com.sccon.geospatial.exception.PessoaNotFoundException;
import com.sccon.geospatial.model.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PessoaServiceTest {

    private PessoaService pessoaService;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        // Usar data fixa para testes determinísticos: 07/02/2023
        fixedClock = Clock.fixed(
            LocalDate.of(2023, 2, 7).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
        );
        pessoaService = new PessoaService(fixedClock);
    }

    @Test
    void listarPessoas_DeveRetornarListaOrdenadaPorNome() {
        List<Pessoa> pessoas = pessoaService.listarPessoas();
        
        assertEquals(3, pessoas.size());
        // Ordem correta em português: João vem antes de José
        assertEquals("João Oliveira", pessoas.get(0).getNome());
        assertEquals("José da Silva", pessoas.get(1).getNome());
        assertEquals("Maria Santos", pessoas.get(2).getNome());
    }

    @Test
    void buscarPorId_QuandoPessoaExiste_DeveRetornarPessoa() {
        Pessoa pessoa = pessoaService.buscarPorId(1L);
        
        assertNotNull(pessoa);
        assertEquals(1L, pessoa.getId());
        assertEquals("José da Silva", pessoa.getNome());
        assertEquals(LocalDate.of(2000, 4, 6), pessoa.getDataNascimento());
        assertEquals(LocalDate.of(2020, 5, 10), pessoa.getDataAdmissao());
    }

    @Test
    void buscarPorId_QuandoPessoaNaoExiste_DeveLancarExcecao() {
        assertThrows(PessoaNotFoundException.class, () -> {
            pessoaService.buscarPorId(999L);
        });
    }

    @Test
    void criarPessoa_ComIdAutomatico_DeveCriarPessoaComProximoId() {
        Pessoa novaPessoa = new Pessoa(null, "Ana Costa", 
            LocalDate.of(1992, 3, 15), 
            LocalDate.of(2022, 6, 1));
        
        Pessoa pessoaCriada = pessoaService.criarPessoa(novaPessoa);
        
        assertEquals(4L, pessoaCriada.getId());
        assertEquals("Ana Costa", pessoaCriada.getNome());

        List<Pessoa> pessoas = pessoaService.listarPessoas();
        assertEquals(4, pessoas.size());
    }

    @Test
    void criarPessoa_ComIdEspecifico_DeveCriarPessoaComIdEspecificado() {
        Pessoa novaPessoa = new Pessoa(5L, "Pedro Silva", 
            LocalDate.of(1985, 7, 20), 
            LocalDate.of(2023, 2, 10));
        
        Pessoa pessoaCriada = pessoaService.criarPessoa(novaPessoa);
        
        assertEquals(5L, pessoaCriada.getId());
        assertEquals("Pedro Silva", pessoaCriada.getNome());
    }

    @Test
    void criarPessoa_ComIdExistente_DeveLancarExcecao() {
        Pessoa pessoaConflito = new Pessoa(1L, "Teste Conflito", 
            LocalDate.of(1990, 1, 1), 
            LocalDate.of(2020, 1, 1));
        
        assertThrows(PessoaConflictException.class, () -> {
            pessoaService.criarPessoa(pessoaConflito);
        });
    }


    @Test
    void atualizarPessoa_QuandoPessoaExiste_DeveAtualizarPessoa() {
        Pessoa pessoaAtualizada = new Pessoa(1L, "José da Silva Atualizado", 
            LocalDate.of(2000, 4, 6), 
            LocalDate.of(2020, 5, 10));
        
        Pessoa resultado = pessoaService.atualizarPessoa(1L, pessoaAtualizada);
        
        assertEquals("José da Silva Atualizado", resultado.getNome());

        Pessoa pessoaVerificada = pessoaService.buscarPorId(1L);
        assertEquals("José da Silva Atualizado", pessoaVerificada.getNome());
    }

    @Test
    void atualizarPessoa_QuandoPessoaNaoExiste_DeveLancarExcecao() {
        Pessoa pessoaAtualizada = new Pessoa(999L, "Teste", 
            LocalDate.of(1990, 1, 1), 
            LocalDate.of(2020, 1, 1));
        
        assertThrows(PessoaNotFoundException.class, () -> {
            pessoaService.atualizarPessoa(999L, pessoaAtualizada);
        });
    }

    @Test
    void removerPessoa_QuandoPessoaExiste_DeveRemoverPessoa() {
        pessoaService.removerPessoa(1L);
        
        assertThrows(PessoaNotFoundException.class, () -> {
            pessoaService.buscarPorId(1L);
        });

        List<Pessoa> pessoas = pessoaService.listarPessoas();
        assertEquals(2, pessoas.size());
    }

    @Test
    void removerPessoa_QuandoPessoaNaoExiste_DeveLancarExcecao() {
        assertThrows(PessoaNotFoundException.class, () -> {
            pessoaService.removerPessoa(999L);
        });
    }

    @Test
    void calcularIdade_EmDias_DeveRetornarIdadeCorreta() {
        // José da Silva: nascido em 06/04/2000, data atual: 07/02/2023
        // Deve retornar 8342 dias
        long idadeEmDias = pessoaService.calcularIdade(1L, "days");
        
        assertEquals(8342, idadeEmDias);
    }

    @Test
    void calcularIdade_EmMeses_DeveRetornarIdadeCorreta() {
        // José da Silva: nascido em 06/04/2000, data atual: 07/02/2023
        // Deve retornar 274 meses
        long idadeEmMeses = pessoaService.calcularIdade(1L, "months");
        
        assertEquals(274, idadeEmMeses);
    }

    @Test
    void calcularIdade_EmAnos_DeveRetornarIdadeCorreta() {
        // José da Silva: nascido em 06/04/2000, data atual: 07/02/2023
        // Deve retornar 22 anos
        long idadeEmAnos = pessoaService.calcularIdade(1L, "years");
        
        assertEquals(22, idadeEmAnos);
    }

    @Test
    void calcularIdade_ComFormatoInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            pessoaService.calcularIdade(1L, "invalid");
        });
    }

    @Test
    void calcularIdade_ComPessoaNaoEncontrada_DeveLancarExcecao() {
        assertThrows(PessoaNotFoundException.class, () -> {
            pessoaService.calcularIdade(999L, "days");
        });
    }

    @Test
    void calcularSalario_EmReais_DeveRetornarSalarioCorreto() {
        // José da Silva: admitido em 10/05/2020, data atual: 07/02/2023
        // 2 anos e 9 meses na empresa
        // Salário base: 1558.00
        // Ano 1: 1558.00 * 1.18 + 500.00 = 2338.44
        // Ano 2: 2338.44 * 1.18 + 500.00 = 3259.36
        double salario = pessoaService.calcularSalario(1L, "full");
        
        assertEquals(3259.36, salario, 0.01);
    }

    @Test
    void calcularSalario_EmSalariosMinimos_DeveRetornarSalarioCorreto() {
        // José da Silva: salário 3259.36, salário mínimo 1302.00
        // 3259.36 / 1302.00 = 2.51
        double salarioEmMinimos = pessoaService.calcularSalario(1L, "min");
        
        assertEquals(2.51, salarioEmMinimos, 0.01);
    }

    @Test
    void calcularSalario_ComFormatoInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            pessoaService.calcularSalario(1L, "invalid");
        });
    }

    @Test
    void calcularSalario_ComPessoaNaoEncontrada_DeveLancarExcecao() {
        assertThrows(PessoaNotFoundException.class, () -> {
            pessoaService.calcularSalario(999L, "full");
        });
    }

    @Test
    void atualizarAtributo_ComNome_DeveAtualizarNome() {
        pessoaService.atualizarAtributo(1L, "nome", "José Silva Modificado");
        
        Pessoa pessoaAtualizada = pessoaService.buscarPorId(1L);
        assertEquals("José Silva Modificado", pessoaAtualizada.getNome());
    }

    @Test
    void atualizarAtributo_ComDataNascimento_DeveAtualizarDataNascimento() {
        LocalDate novaData = LocalDate.of(2000, 4, 7);
        pessoaService.atualizarAtributo(1L, "dataNascimento", novaData);
        
        Pessoa pessoaAtualizada = pessoaService.buscarPorId(1L);
        assertEquals(novaData, pessoaAtualizada.getDataNascimento());
    }

    @Test
    void atualizarAtributo_ComAtributoInvalido_DeveLancarExcecao() {
        assertThrows(InvalidParameterException.class, () -> {
            pessoaService.atualizarAtributo(1L, "atributoInvalido", "valor");
        });
    }

    @Test
    void atualizarAtributo_ComNomeVazio_DeveLancarExcecao() {
        assertThrows(InvalidParameterException.class, () -> {
            pessoaService.atualizarAtributo(1L, "nome", "");
        });
    }

    @Test
    void criarPessoa_ComPessoaNula_DeveLancarExcecao() {
        assertThrows(InvalidParameterException.class, () -> {
            pessoaService.criarPessoa(null);
        });
    }
}
