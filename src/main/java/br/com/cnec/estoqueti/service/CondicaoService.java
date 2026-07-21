package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Condicao;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.CondicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CondicaoService {

    private final CondicaoRepository condicaoRepository;

    public CondicaoService(CondicaoRepository condicaoRepository) {
        this.condicaoRepository = condicaoRepository;
    }

    public Condicao cadastrar(Condicao condicao) {
        if (condicao == null) {
            throw new RegraNegocioException("A condição é obrigatória.");
        }

        String nomeNormalizado = normalizarNome(condicao.getNome());

        validarExistencia(nomeNormalizado);

        condicao.setId(null);
        condicao.setNome(nomeNormalizado);
        condicao.setAtivo(true);

        return condicaoRepository.save(condicao);
    }

    public List<Condicao> listarTodas() {
        return condicaoRepository.findAll();
    }

    public List<Condicao> listarAtivas() {
        return condicaoRepository.findAllByAtivoTrue();
    }

    public List<Condicao> listarDesativadas() {
        return condicaoRepository.findAllByAtivoFalse();
    }

    public Condicao buscarPorId(Long idCondicao) {
        validarId(idCondicao);

        return condicaoRepository.findById(idCondicao)
                .orElseThrow(
                        () -> new RegraNegocioException(
                                "Condição não encontrada."
                        )
                );
    }

    public Condicao ativarCondicao(Long idCondicao) {
        Condicao condicao = buscarPorId(idCondicao);

        if (Boolean.TRUE.equals(condicao.getAtivo())) {
            throw new RegraNegocioException(
                    "A condição já está ativa."
            );
        }

        condicao.setAtivo(true);

        return condicaoRepository.save(condicao);
    }

    public Condicao desativarCondicao(Long idCondicao) {
        Condicao condicao = buscarPorId(idCondicao);

        if (Boolean.FALSE.equals(condicao.getAtivo())) {
            throw new RegraNegocioException(
                    "A condição já está desativada."
            );
        }

        condicao.setAtivo(false);

        return condicaoRepository.save(condicao);
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException(
                    "O nome da condição é obrigatório."
            );
        }

        return nome.trim();
    }

    private void validarExistencia(String nome) {
        if (condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoTrue(nome)) {

            throw new RegraNegocioException(
                    "Já existe uma condição ativa com esse nome."
            );
        }

        if (condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoFalse(nome)) {

            throw new RegraNegocioException(
                    "Essa condição já existe, mas está desativada."
            );
        }
    }

    private void validarId(Long idCondicao) {
        if (idCondicao == null || idCondicao <= 0) {
            throw new RegraNegocioException(
                    "O ID da condição é inválido."
            );
        }
    }
}