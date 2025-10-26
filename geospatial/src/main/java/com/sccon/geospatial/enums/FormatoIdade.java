package com.sccon.geospatial.enums;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public enum FormatoIdade {
    DAYS {
        @Override
        public long calcular(LocalDate dataNascimento, LocalDate dataAtual) {
            return ChronoUnit.DAYS.between(dataNascimento, dataAtual);
        }
    },
    
    MONTHS {
        @Override
        public long calcular(LocalDate dataNascimento, LocalDate dataAtual) {
            return ChronoUnit.MONTHS.between(dataNascimento, dataAtual);
        }
    },
    
    YEARS {
        @Override
        public long calcular(LocalDate dataNascimento, LocalDate dataAtual) {
            return Period.between(dataNascimento, dataAtual).getYears();
        }
    };
    
    public abstract long calcular(LocalDate dataNascimento, LocalDate dataAtual);
    
}
