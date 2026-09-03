package br.com.fiap.reservas.controller;

import br.com.fiap.reservas.entity.Equipamento;
import br.com.fiap.reservas.service.EquipamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoService service;

    public EquipamentoController(EquipamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Equipamento> listar() {
        return service.listar();
    }

    @GetMapping("/ativos")
    public List<Equipamento> listarAtivos() {
        return service.listarAtivos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipamento> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Equipamento> cadastrar(@Valid @RequestBody Equipamento equipamento) {
        return ResponseEntity.ok(service.salvar(equipamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipamento> atualizar(@PathVariable Long id, @Valid @RequestBody Equipamento equipamento) {
        return ResponseEntity.ok(service.atualizar(id, equipamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
