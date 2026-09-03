package br.com.fiap.reservas.service;

import br.com.fiap.reservas.entity.Curso;
import br.com.fiap.reservas.exception.RecursoNaoEncontradoException;
import br.com.fiap.reservas.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository repository;

    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }

    public List<Curso> listar() {
        return repository.findAll();
    }

    public Curso buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado: " + id));
    }

    public Curso salvar(Curso curso) {
        return repository.save(curso);
    }

    public void excluir(Long id) {
        Curso curso = buscarPorId(id);
        repository.delete(curso);
    }
}
