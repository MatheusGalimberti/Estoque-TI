package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Condicao;
import br.com.cnec.estoqueti.entity.Item;
import br.com.cnec.estoqueti.entity.RegistroCondicao;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.CondicaoRepository;
import br.com.cnec.estoqueti.repository.ItemRepository;
import br.com.cnec.estoqueti.repository.RegistroCondicaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistroCondicaoService {

    private final RegistroCondicaoRepository registroCondicaoRepository;
    private final CondicaoRepository condicaoRepository;
    private final ItemRepository itemRepository;

    public RegistroCondicaoService(
            RegistroCondicaoRepository registroCondicaoRepository,
            CondicaoRepository condicaoRepository,
            ItemRepository itemRepository
    ) {
        this.registroCondicaoRepository = registroCondicaoRepository;
        this.condicaoRepository = condicaoRepository;
        this.itemRepository = itemRepository;
    }

    public RegistroCondicao cadastrar(
            Long itemId,
            Long condicaoId,
            String observacao
    ) {
        Item item = buscarItem(itemId);
        Condicao condicao = buscarCondicao(condicaoId);

        validarCondicaoAtiva(condicao);
        validarCondicaoPendenteDuplicada(itemId, condicaoId);

        RegistroCondicao registro = RegistroCondicao.builder()
                .item(item)
                .condicao(condicao)
                .observacao(normalizarObservacaoOpcional(observacao))
                .iniciadaEm(LocalDateTime.now())
                .build();

        return registroCondicaoRepository.save(registro);
    }

    public List<RegistroCondicao> listarHistoricoCondicoes(Long itemId) {
        buscarItem(itemId);

        return registroCondicaoRepository.findAllByItem_Id(itemId);
    }

    public List<RegistroCondicao> listarCondicoesPendentes(Long itemId) {
        buscarItem(itemId);

        return registroCondicaoRepository
                .findAllByItem_IdAndResolvidaEmIsNull(itemId);
    }

    public RegistroCondicao editarObservacao(
            Long idItem,
            Long idRegistro,
            String novaObservacao
    ) {
        if (novaObservacao == null || novaObservacao.isBlank()) {
            throw new RegraNegocioException(
                    "A observação não pode ser vazia."
            );
        }

        RegistroCondicao registro =
                buscarRegistroNaoResolvido(idItem, idRegistro);

        registro.setObservacao(novaObservacao.trim());

        return registroCondicaoRepository.save(registro);
    }

    public RegistroCondicao resolverCondicao(
            Long itemId,
            Long registroId
    ) {
        RegistroCondicao registro =
                buscarRegistroNaoResolvido(itemId, registroId);

        registro.setResolvidaEm(LocalDateTime.now());

        return registroCondicaoRepository.save(registro);
    }

    private RegistroCondicao buscarRegistroNaoResolvido(
            Long itemId,
            Long registroId
    ) {
        validarId(itemId, "item");
        validarId(registroId, "registro");

        RegistroCondicao registro = registroCondicaoRepository
                .findByIdAndItem_Id(registroId, itemId)
                .orElseThrow(
                        () -> new RegraNegocioException(
                                "Registro de condição não encontrado para esse item."
                        )
                );

        if (registro.getResolvidaEm() != null) {
            throw new RegraNegocioException(
                    "A condição já foi resolvida."
            );
        }

        return registro;
    }

    private Item buscarItem(Long itemId) {
        validarId(itemId, "item");

        return itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new RegraNegocioException(
                                "Item não encontrado."
                        )
                );
    }

    private Condicao buscarCondicao(Long condicaoId) {
        validarId(condicaoId, "condição");

        return condicaoRepository.findById(condicaoId)
                .orElseThrow(
                        () -> new RegraNegocioException(
                                "Condição não encontrada."
                        )
                );
    }

    private void validarCondicaoAtiva(Condicao condicao) {
        if (!Boolean.TRUE.equals(condicao.getAtivo())) {
            throw new RegraNegocioException(
                    "Não é possível utilizar uma condição desativada."
            );
        }
    }

    private void validarCondicaoPendenteDuplicada(
            Long itemId,
            Long condicaoId
    ) {
        boolean existeRegistroPendente = registroCondicaoRepository
                .existsByItem_IdAndCondicao_IdAndResolvidaEmIsNull(
                        itemId,
                        condicaoId
                );

        if (existeRegistroPendente) {
            throw new RegraNegocioException(
                    "Essa condição já está pendente para o item."
            );
        }
    }

    private String normalizarObservacaoOpcional(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }

        return observacao.trim();
    }

    private void validarId(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new RegraNegocioException(
                    "O ID do " + campo + " é inválido."
            );
        }
    }
}