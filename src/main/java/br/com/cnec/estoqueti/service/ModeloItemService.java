package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Categoria;
import br.com.cnec.estoqueti.entity.ModeloItem;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.CategoriaRepository;
import br.com.cnec.estoqueti.repository.ModeloItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ModeloItemService {

    private final ModeloItemRepository modeloItemRepository;
    private final CategoriaRepository categoriaRepository;


    public ModeloItemService(
            ModeloItemRepository modeloItemRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.modeloItemRepository = modeloItemRepository;
        this.categoriaRepository = categoriaRepository;
    }


    public ModeloItem cadastrar (ModeloItem modeloItem) {
        validarModelo(modeloItem);

        String nomeNormalizado = modeloItem.getNome().trim();
        boolean isNomePresente = modeloItemRepository.existsByNomeIgnoreCaseAndAtivoTrue(nomeNormalizado);
        if (isNomePresente) {
            throw new RegraNegocioException(
                    "Já existe um modelo de item ativo com esse nome."
            );
        }

        Categoria categoria = categoriaRepository.findById(
                        modeloItem.getCategoria().getId()
        ).orElseThrow(()-> new RegraNegocioException("Categoria não encontrada."));

        modeloItem.setId(null);
        modeloItem.setNome(nomeNormalizado);
        modeloItem.setCategoria(categoria);
        modeloItem.setAtivo(true);

        return  modeloItemRepository.save(modeloItem);
    }

        private void validarModelo (ModeloItem modeloItem){
            if (modeloItem == null) {
                throw new RegraNegocioException("O modelo do item é obrigatório.");
            }

            if (modeloItem.getNome() == null || modeloItem.getNome().isBlank()) {
                throw new RegraNegocioException("O nome do modelo é obrigatório.");
            }

            if (modeloItem.getCategoria() == null
                    || modeloItem.getCategoria().getId() == null) {
                throw new RegraNegocioException("A categoria é obrigatória.");
            }
        }


    }
