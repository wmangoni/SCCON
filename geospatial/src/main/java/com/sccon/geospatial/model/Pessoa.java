package com.sccon.geospatial.model;

import com.sccon.geospatial.exception.InvalidParameterException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class Pessoa {

    private static final String NOME_VAZIO_ERROR_MESSAGE = "Nome não pode ser vazio";
    private static final String DATA_NASC_ERROR_MESSAGE = "Data de nascimento não pode ser nula";
    private static final String DATA_ADMISSAO_ERROR_MESSAGE = "Data de admissão não pode ser nula";

    private Long id;
    
    @NotBlank(message = NOME_VAZIO_ERROR_MESSAGE)
    private String nome;
    
    @NotNull(message = DATA_NASC_ERROR_MESSAGE)
    private LocalDate dataNascimento;
    
    @NotNull(message = DATA_ADMISSAO_ERROR_MESSAGE)
    private LocalDate dataAdmissao;

    public Pessoa() {}

    public Pessoa(Long id, String nome, LocalDate dataNascimento, LocalDate dataAdmissao) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.dataAdmissao = dataAdmissao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public void validaNome() {
        if (this.nome == null || this.nome.trim().isEmpty()) {
            throw new InvalidParameterException(NOME_VAZIO_ERROR_MESSAGE);
        }
    }

    public void validaDataNescimento() {
        if (this.dataNascimento == null) {
            throw new InvalidParameterException(DATA_NASC_ERROR_MESSAGE);
        }
    }

    public void validaDataAdminissao() {
        if (this.dataAdmissao == null) {
            throw new InvalidParameterException(DATA_ADMISSAO_ERROR_MESSAGE);
        }
    }
}
