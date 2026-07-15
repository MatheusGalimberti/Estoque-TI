package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Categoria;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Categoria cadastrar(Categoria categoria) {
        if (categoria == null) {
            throw new RegraNegocioException("A categoria é obrigatória.");
        }

        String nomeNormalizado = normalizarNome(categoria.getNome());

        validarExistencia(nomeNormalizado);

        categoria.setId(null);
        categoria.setNome(nomeNormalizado);
        categoria.setAtivo(true);

        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarAtivas() {
        return categoriaRepository.findAllByAtivoTrue();
    }

    public Categoria buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException(
                    "O ID da categoria é obrigatório."
            );
        }

        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RegraNegocioException(
                                "Categoria não encontrada."
                        )
                );
    }

    private void validarExistencia(String nome) {
        if (categoriaRepository
                .existsByNomeIgnoreCaseAndAtivoTrue(nome)) {
            throw new RegraNegocioException(
                    "Já existe uma categoria ativa com esse nome."
            );
        }

        if (categoriaRepository
                .existsByNomeIgnoreCaseAndAtivoFalse(nome)) {
            throw new RegraNegocioException(
                    "Essa categoria já existe, mas está desativada."
            );
        }
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException(
                    "O nome da categoria é obrigatório."
            );
        }

        return nome.trim();
    }
}