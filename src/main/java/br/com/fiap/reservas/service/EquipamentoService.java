package br.com.fiap.reservas.service;

import br.com.fiap.reservas.entity.Equipamento;
import br.com.fiap.reservas.exception.RecursoNaoEncontradoException;
import br.com.fiap.reservas.repository.EquipamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipamentoService {

    private final EquipamentoRepository repository;

    public EquipamentoService(EquipamentoRepository repository) {
        this.repository = repository;
    }

    public List<Equipamento> listar() {
        return repository.findAll();
    }

    public List<Equipamento> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public Equipamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento não encontrado: " + id));
    }

    public Equipamento salvar(Equipamento equipamento) {
        return repository.save(equipamento);
    }

    public Equipamento atualizar(Long id, Equipamento dados) {
        Equipamento existente = buscarPorId(id);
        existente.setIdentificacao(dados.getIdentificacao());
        existente.setTipo(dados.getTipo());
        existente.setDescricao(dados.getDescricao());
        existente.setAtivo(dados.getAtivo());
        return repository.save(existente);
    }

    public void excluir(Long id) {
        Equipamento equipamento = buscarPorId(id);
        repository.delete(equipamento);
    }
}
