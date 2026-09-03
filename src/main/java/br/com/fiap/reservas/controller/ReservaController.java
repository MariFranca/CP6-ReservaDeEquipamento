package br.com.fiap.reservas.controller;

import br.com.fiap.reservas.dto.ReservaRequestDTO;
import br.com.fiap.reservas.dto.ReservaResponseDTO;
import br.com.fiap.reservas.entity.Reserva;
import br.com.fiap.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReservaResponseDTO> listar() {
        return service.listar().stream()
                .map(ReservaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ReservaResponseDTO.fromEntity(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> reservar(@Valid @RequestBody ReservaRequestDTO dto) {
        Reserva reserva = service.criar(dto);
        return ResponseEntity.ok(ReservaResponseDTO.fromEntity(reserva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
