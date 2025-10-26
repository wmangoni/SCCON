package com.sccon.geospatial.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatoSalarioTest {

    private final double salarioAtual = 3259.36;
    private final double salarioMinimo = 1302.00;

    @Test
    void calcularSalario_Full_DeveRetornarSalarioEmReais() {
        double resultado = FormatoSalario.FULL.calcular(salarioAtual, salarioMinimo);
        assertEquals(3259.36, resultado, 0.01);
    }

    @Test
    void calcularSalario_Min_DeveRetornarSalarioEmMinimos() {
        double resultado = FormatoSalario.MIN.calcular(salarioAtual, salarioMinimo);
        assertEquals(2.51, resultado, 0.01);
    }

    @Test
    void valueOf_ComFormatoValido_DeveRetornarEnumCorreto() {
        assertEquals(FormatoSalario.FULL, FormatoSalario.valueOf("FULL"));
        assertEquals(FormatoSalario.MIN, FormatoSalario.valueOf("MIN"));
    }

    @Test
    void valueOf_ComFormatoInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            FormatoSalario.valueOf("INVALID");
        });
    }

    @Test
    void valueOf_ComFormatoNulo_DeveLancarExcecao() {
        assertThrows(NullPointerException.class, () -> {
            FormatoSalario.valueOf(null);
        });
    }
}
