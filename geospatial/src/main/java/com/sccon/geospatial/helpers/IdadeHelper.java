package com.sccon.geospatial.helpers;

import com.sccon.geospatial.enums.FormatoIdade;
import com.sccon.geospatial.model.Pessoa;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
public class IdadeHelper {
    
    private final Clock clock;
    
    public IdadeHelper(Clock clock) {
        this.clock = clock;
    }
    
    public IdadeHelper() {
        this(Clock.systemDefaultZone());
    }
    
    public long calcularIdade(Pessoa pessoa, String formato) {
        pessoa.validaDataNescimento();
        
        LocalDate hoje = LocalDate.now(clock);
        LocalDate dataNascimento = pessoa.getDataNascimento();
        
        FormatoIdade formatoIdade = FormatoIdade.valueOf(formato.toUpperCase());
        return formatoIdade.calcular(dataNascimento, hoje);
    }
}
