package br.com.fiap.reservas.controller;

import br.com.fiap.reservas.entity.Sala;
import br.com.fiap.reservas.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaController {

    private final SalaService service;

    public SalaController(SalaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sala> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sala> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Sala> cadastrar(@Valid @RequestBody Sala sala) {
        return ResponseEntity.ok(service.salvar(sala));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
