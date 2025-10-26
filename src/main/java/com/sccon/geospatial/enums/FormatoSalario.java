package com.sccon.geospatial.enums;

public enum FormatoSalario {
    FULL {
        @Override
        public double calcular(double salarioAtual, double salarioMinimo) {
            return Math.ceil(salarioAtual * 100) / 100;
        }
    },
    
    MIN {
        @Override
        public double calcular(double salarioAtual, double salarioMinimo) {
            double salariosMinimos = salarioAtual / salarioMinimo;
            return Math.ceil(salariosMinimos * 100) / 100;
        }
    };
    
    public abstract double calcular(double salarioAtual, double salarioMinimo);
    
}
