package com.sccon.geospatial.enums;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FormatoIdadeTest {

    private final LocalDate dataNascimento = LocalDate.of(2000, 4, 6);
    private final LocalDate dataAtual = LocalDate.of(2023, 2, 7);

    @Test
    void calcularIdade_EmDias_DeveRetornarDiasCorretos() {
        long resultado = FormatoIdade.DAYS.calcular(dataNascimento, dataAtual);
        assertEquals(8342, resultado);
    }

    @Test
    void calcularIdade_EmMeses_DeveRetornarMesesCorretos() {
        long resultado = FormatoIdade.MONTHS.calcular(dataNascimento, dataAtual);
        assertEquals(274, resultado);
    }

    @Test
    void calcularIdade_EmAnos_DeveRetornarAnosCorretos() {
        long resultado = FormatoIdade.YEARS.calcular(dataNascimento, dataAtual);
        assertEquals(22, resultado);
    }

    @Test
    void valueOf_ComFormatoValido_DeveRetornarEnumCorreto() {
        assertEquals(FormatoIdade.DAYS, FormatoIdade.valueOf("DAYS"));
        assertEquals(FormatoIdade.MONTHS, FormatoIdade.valueOf("MONTHS"));
        assertEquals(FormatoIdade.YEARS, FormatoIdade.valueOf("YEARS"));
    }

    @Test
    void valueOf_ComFormatoInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            FormatoIdade.valueOf("INVALID");
        });
    }

    @Test
    void valueOf_ComFormatoNulo_DeveLancarExcecao() {
        assertThrows(NullPointerException.class, () -> {
            FormatoIdade.valueOf(null);
        });
    }
}
