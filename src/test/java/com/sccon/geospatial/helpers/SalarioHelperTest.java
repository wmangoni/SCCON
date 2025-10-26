package com.sccon.geospatial.helpers;

import com.sccon.geospatial.model.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class SalarioHelperTest {

    private SalarioHelper salarioHelper;
    private Pessoa pessoaTeste;

    @BeforeEach
    void setUp() {
        // Usar data fixa para testes determinísticos: 07/02/2023
        Clock fixedClock = Clock.fixed(
            LocalDate.of(2023, 2, 7).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
        );
        salarioHelper = new SalarioHelper(fixedClock);
        
        pessoaTeste = new Pessoa(1L, "José da Silva", 
            LocalDate.of(2000, 4, 6), 
            LocalDate.of(2020, 5, 10));
    }

    @Test
    void calcularSalario_EmReais_DeveRetornarSalarioCorreto() {
        // José da Silva: admitido em 10/05/2020, data atual: 07/02/2023
        // 2 anos e 9 meses na empresa
        // Salário base: 1558.00
        // Ano 1: 1558.00 * 1.18 + 500.00 = 2338.44
        // Ano 2: 2338.44 * 1.18 + 500.00 = 3259.36
        double resultado = salarioHelper.calcularSalario(pessoaTeste, "full");
        assertEquals(3259.36, resultado, 0.01);
    }

    @Test
    void calcularSalario_EmSalariosMinimos_DeveRetornarSalarioCorreto() {
        // José da Silva: salário 3259.36, salário mínimo 1302.00
        // 3259.36 / 1302.00 = 2.51
        double resultado = salarioHelper.calcularSalario(pessoaTeste, "min");
        assertEquals(2.51, resultado, 0.01);
    }

    @Test
    void calcularSalario_ComFormatoInvalido_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            salarioHelper.calcularSalario(pessoaTeste, "invalid");
        });
    }

    @Test
    void calcularSalario_ComDataAdmissaoNula_DeveLancarExcecao() {
        Pessoa pessoaComDataNula = new Pessoa(2L, "Teste", LocalDate.of(1990, 1, 1), null);
        
        assertThrows(Exception.class, () -> {
            salarioHelper.calcularSalario(pessoaComDataNula, "full");
        });
    }

    @Test
    void calcularSalario_ComPessoaRecemAdmitida_DeveRetornarSalarioBase() {
        Pessoa pessoaRecemAdmitida = new Pessoa(3L, "Novo Funcionário", 
            LocalDate.of(1990, 1, 1), 
            LocalDate.of(2023, 2, 7));
        
        double resultado = salarioHelper.calcularSalario(pessoaRecemAdmitida, "full");
        assertEquals(1558.00, resultado, 0.01);
    }
}
