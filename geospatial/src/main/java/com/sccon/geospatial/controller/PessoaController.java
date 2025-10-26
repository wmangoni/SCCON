package com.sccon.geospatial.controller;

import com.sccon.geospatial.model.Pessoa;
import com.sccon.geospatial.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/person")
public class PessoaController {
    
    private final PessoaService pessoaService;
    
    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }
    
    @GetMapping
    public ResponseEntity<List<Pessoa>> listarPessoas() {
        List<Pessoa> pessoas = pessoaService.listarPessoas();
        return ResponseEntity.ok(pessoas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> buscarPorId(@PathVariable Long id) {
        Pessoa pessoa = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(pessoa);
    }
    
    @PostMapping
    public ResponseEntity<Pessoa> criarPessoa(@Valid @RequestBody Pessoa pessoa) {
        Pessoa pessoaCriada = pessoaService.criarPessoa(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaCriada);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> atualizarPessoa(@PathVariable Long id, @Valid @RequestBody Pessoa pessoa) {
        Pessoa pessoaAtualizada = pessoaService.atualizarPessoa(id, pessoa);
        return ResponseEntity.ok(pessoaAtualizada);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<Pessoa> atualizarAtributo(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String atributo = entry.getKey();
            Object valor = entry.getValue();
            
            if (atributo.equals("dataNascimento") || atributo.equals("dataAdmissao")) {
                if (valor != null) {
                    valor = LocalDate.parse(valor.toString());
                }
            }
            
            pessoaService.atualizarAtributo(id, atributo, valor);
        }

        return ResponseEntity.ok(pessoaService.buscarPorId(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPessoa(@PathVariable Long id) {
        pessoaService.removerPessoa(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/age")
    public ResponseEntity<Long> calcularIdade(@PathVariable Long id, @RequestParam String output) {
        return ResponseEntity.ok(pessoaService.calcularIdade(id, output));
    }
    
    @GetMapping("/{id}/salary")
    public ResponseEntity<Double> calcularSalario(@PathVariable Long id, @RequestParam String output) {
        return ResponseEntity.ok(pessoaService.calcularSalario(id, output));
    }
}
