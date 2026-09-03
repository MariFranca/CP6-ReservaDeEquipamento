package br.com.fiap.reservas.service;

import br.com.fiap.reservas.entity.Professor;
import br.com.fiap.reservas.exception.RecursoNaoEncontradoException;
import br.com.fiap.reservas.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository repository;

    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
    }

    public List<Professor> listar() {
        return repository.findAll();
    }

    public Professor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Professor não encontrado: " + id));
    }

    public Professor salvar(Professor professor) {
        return repository.save(professor);
    }

    public void excluir(Long id) {
        Professor professor = buscarPorId(id);
        repository.delete(professor);
    }
}
