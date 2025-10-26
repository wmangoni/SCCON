package com.sccon.geospatial.helpers;

import com.sccon.geospatial.model.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class IdadeHelperTest {

    private IdadeHelper idadeHelper;
    private Pessoa pessoaTeste;

    @BeforeEach
    void setUp() {
        // Usar data fixa para testes determinísticos: 07/02/2023
        Clock fixedClock = Clock.fixed(
            LocalDate.of(2023, 2, 7).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
        );
        idadeHelper = new IdadeHelper(fixedClock);
        
        pessoaTeste = new Pessoa(1L, "José da Silva", 
            LocalDate.of(2000, 4, 6), 
            LocalDate.of(2020, 5, 10));
    }

    @Test
    void calcularIdade_EmDias_DeveRetornarIdadeCorreta() {
        long resultado = idadeHelper.calcularIdade(pessoaTeste, "days");
        assertEquals(8342, resultado);
    }

    @Test
    void calcularIdade_EmMeses_DeveRetornarIdadeCorreta() {
        long resultado = idadeHelper.calcularIdade(pessoaTeste, "months");
        assertEquals(274, resultado);
    }

    @Test
    void calcularIdade_EmAnos_DeveRetornarIdadeCorreta() {
        long resultado = idadeHelper.calcularIdade(pessoaTeste, "years");
        assertEquals(22, resultado);
    }

    @Test
    void calcularIdade_ComFormatoInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            idadeHelper.calcularIdade(pessoaTeste, "invalid");
        });
    }

    @Test
    void calcularIdade_ComDataNascimentoNula_DeveLancarExcecao() {
        Pessoa pessoaComDataNula = new Pessoa(2L, "Teste", null, LocalDate.of(2020, 1, 1));
        
        assertThrows(Exception.class, () -> {
            idadeHelper.calcularIdade(pessoaComDataNula, "days");
        });
    }
}
