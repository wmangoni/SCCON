package com.sccon.geospatial.helpers;

import com.sccon.geospatial.enums.FormatoSalario;
import com.sccon.geospatial.model.Pessoa;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

@Component
public class SalarioHelper {

    private static final double SALARIO_BASE = 1558.00;
    private static final double MULTIPLICADOR_ANUAL = 1.18;
    private static final double BONUS_ANUAL = 500.00;
    private static final double SALARIO_MINIMO = 1302.00;
    
    private final Clock clock;
    
    public SalarioHelper(Clock clock) {
        this.clock = clock;
    }
    
    public SalarioHelper() {
        this(Clock.systemDefaultZone());
    }
    
    public double calcularSalario(Pessoa pessoa, String formato) {
        pessoa.validaDataAdminissao();
        
        LocalDate hoje = LocalDate.now(clock);
        LocalDate dataAdmissao = pessoa.getDataAdmissao();
        
        int anosNaEmpresa = Period.between(dataAdmissao, hoje).getYears();
        double salarioAtual = calcularSalarioAtual(anosNaEmpresa);
        
        FormatoSalario formatoSalario = FormatoSalario.valueOf(formato.toUpperCase());
        return formatoSalario.calcular(salarioAtual, SALARIO_MINIMO);
    }
    
    private double calcularSalarioAtual(int anosNaEmpresa) {
        double salarioAtual = SALARIO_BASE;
        
        for (int i = 0; i < anosNaEmpresa; i++) {
            salarioAtual = salarioAtual * MULTIPLICADOR_ANUAL + BONUS_ANUAL;
        }
        
        return salarioAtual;
    }
}
