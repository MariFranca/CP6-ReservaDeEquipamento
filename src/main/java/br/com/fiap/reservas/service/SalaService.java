package br.com.fiap.reservas.service;

import br.com.fiap.reservas.entity.Sala;
import br.com.fiap.reservas.exception.RecursoNaoEncontradoException;
import br.com.fiap.reservas.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    private final SalaRepository repository;

    public SalaService(SalaRepository repository) {
        this.repository = repository;
    }

    public List<Sala> listar() {
        return repository.findAll();
    }

    public Sala buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada: " + id));
    }

    public Sala salvar(Sala sala) {
        return repository.save(sala);
    }

    public void excluir(Long id) {
        Sala sala = buscarPorId(id);
        repository.delete(sala);
    }
}
