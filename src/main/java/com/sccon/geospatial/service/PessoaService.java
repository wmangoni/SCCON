package com.sccon.geospatial.service;

import com.sccon.geospatial.exception.InvalidParameterException;
import com.sccon.geospatial.exception.PessoaConflictException;
import com.sccon.geospatial.exception.PessoaNotFoundException;
import com.sccon.geospatial.helpers.IdadeHelper;
import com.sccon.geospatial.helpers.SalarioHelper;
import com.sccon.geospatial.model.Pessoa;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PessoaService {
    
    private final Map<Long, Pessoa> pessoas = new ConcurrentHashMap<>();
    private Long proximoId = 1L;
    private final IdadeHelper idadeHelper;
    private final SalarioHelper salarioHelper;
    private final Collator collator = Collator.getInstance(Locale.forLanguageTag("pt-BR"));
    
    public PessoaService() {
        this(Clock.systemDefaultZone());
    }
    
    public PessoaService(Clock clock) {
        this.idadeHelper = new IdadeHelper(clock);
        this.salarioHelper = new SalarioHelper(clock);
        inicializarDados();
    }
    
    private void inicializarDados() {
        Pessoa pessoa1 = new Pessoa(1L, "José da Silva", LocalDate.of(2000, 4, 6), LocalDate.of(2020, 5, 10));
        Pessoa pessoa2 = new Pessoa(2L, "Maria Santos", LocalDate.of(1995, 8, 15), LocalDate.of(2019, 3, 20));
        Pessoa pessoa3 = new Pessoa(3L, "João Oliveira", LocalDate.of(1988, 12, 3), LocalDate.of(2021, 1, 15));
        
        pessoas.put(1L, pessoa1);
        pessoas.put(2L, pessoa2);
        pessoas.put(3L, pessoa3);
        
        proximoId = 4L;
    }
    
    public List<Pessoa> listarPessoas() {
        return pessoas.values().stream()
                .sorted(Comparator.comparing(Pessoa::getNome, collator))
                .collect(Collectors.toList());
    }
    
    public Pessoa buscarPorId(Long id) {
        Pessoa pessoa = pessoas.get(id);
        if (pessoa == null) {
            throw new PessoaNotFoundException("Pessoa com ID " + id + " não encontrada");
        }
        return pessoa;
    }
    
    public Pessoa criarPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            throw new InvalidParameterException("Pessoa não pode ser nula");
        }
        
        if (pessoa.getId() == null) {
            pessoa.setId(proximoId++);
        } else {
            if (pessoas.containsKey(pessoa.getId())) {
                throw new PessoaConflictException("Pessoa com ID " + pessoa.getId() + " já existe");
            }
            if (pessoa.getId() >= proximoId) {
                proximoId = pessoa.getId() + 1;
            }
        }
        pessoas.put(pessoa.getId(), pessoa);
        return pessoa;
    }
    
    public Pessoa atualizarPessoa(Long id, Pessoa pessoaAtualizada) {
        buscarPorId(id);
        pessoaAtualizada.setId(id);
        pessoas.put(id, pessoaAtualizada);
        return pessoaAtualizada;
    }
    
    public Pessoa atualizarAtributo(Long id, String prop, Object val) {
        Pessoa pessoa = buscarPorId(id);
        
        switch (prop.toLowerCase()) {
            case "nome":
                pessoa.setNome((String) val);
                pessoa.validaNome();
                break;
            case "datanascimento":
                pessoa.setDataNascimento((LocalDate) val);
                pessoa.validaDataNescimento();
                break;
            case "dataadmissao":
                pessoa.setDataAdmissao((LocalDate) val);
                pessoa.validaDataAdminissao();
                break;
            default:
                throw new InvalidParameterException("Atributo '" + prop + "' não é válido");
        }
        
        return pessoa;
    }
    
    public void removerPessoa(Long id) {
        buscarPorId(id);
        pessoas.remove(id);
    }
    
    public long calcularIdade(Long id, String formato) {
        Pessoa pessoa = buscarPorId(id);
        return idadeHelper.calcularIdade(pessoa, formato);
    }
    
    public double calcularSalario(Long id, String formato) {
        Pessoa pessoa = buscarPorId(id);
        return salarioHelper.calcularSalario(pessoa, formato);
    }
}
