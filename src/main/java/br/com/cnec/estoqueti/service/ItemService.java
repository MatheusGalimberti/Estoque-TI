package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Item;
import br.com.cnec.estoqueti.entity.ItemComponente;
import br.com.cnec.estoqueti.entity.Local;
import br.com.cnec.estoqueti.entity.ModeloItem;
import br.com.cnec.estoqueti.entity.RegistroCondicao;
import br.com.cnec.estoqueti.enums.StatusItem;
import br.com.cnec.estoqueti.enums.TipoControleItem;
import br.com.cnec.estoqueti.enums.TipoRegistroItem;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.ItemRepository;
import br.com.cnec.estoqueti.repository.LocalRepository;
import br.com.cnec.estoqueti.repository.ModeloItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ModeloItemRepository modeloItemRepository;
    private final LocalRepository localRepository;
    private final RegistroCondicaoService registroCondicaoService;


    public ItemService(
            ItemRepository itemRepository,
            ModeloItemRepository modeloItemRepository,
            LocalRepository localRepository,
            RegistroCondicaoService registroCondicaoService
    ) {
        this.itemRepository = itemRepository;
        this.modeloItemRepository = modeloItemRepository;
        this.localRepository = localRepository;
        this.registroCondicaoService = registroCondicaoService;
    }

    public Item cadastrar(
            Long idModelo, Long idLocal, int quantidade,
            TipoControleItem tipoControle, TipoRegistroItem tipoRegistro,
            String patrimonio, String observacao, StatusItem statusItem,
            String numeroSerie) {

        if (idModelo == null || idModelo <= 0) throw new RegraNegocioException("ID do modelo inválido");

        if (idLocal == null || idLocal <= 0) throw new RegraNegocioException("ID do local inválido");

        if (tipoControle == null) throw new RegraNegocioException("Tipo é obrigatório");

        if (statusItem == null) throw new RegraNegocioException("O status é obrigatório");

        if (quantidade < 0) throw new RegraNegocioException("Quantidade não pode ser negativa");

        if (quantidade == 0) quantidade = 1;

        validarTipoRegistroComQuantidade(tipoRegistro, quantidade);

        Item item = Item.builder()
                .modeloItem(buscarModeloPorId(idModelo))
                .statusItem(statusItem)
                .localAtual(buscarLocalPorId(idLocal))
                .observacoes(observacao)
                .tipoRegistro(tipoRegistro)
                .numeroSerie(numeroSerie)
                .quantidade(quantidade)
                .tipoControle(tipoControle)
                .patrimonio(patrimonio)
                .build();


        return itemRepository.save(item);
    }

    @Transactional
    public Item mudarStatus(Long idItem, StatusItem status) {

        if(status == null){
            throw new RegraNegocioException("status é obrigatório");
        }

        Item item = buscarItemPorId(idItem);

        item.setStatusItem(status);

        return item;
    }

    @Transactional
    public Item moverItem(Long idLocal, Long idItem) {
        Item item = buscarItemPorId(idItem);

        Local local = buscarLocalPorId(idLocal);

        item.setLocalAtual(local);
        return item;
    }

    @Transactional
    public void adicionarCondicao(Long idItem, Long idCondicao,
            String observacao
    ) {
        buscarItemPorId(idItem);

        registroCondicaoService.cadastrar(
                idItem,
                idCondicao,
                observacao
        );
    }

    @Transactional
    public void anexarComponenteAoItemPai(Long idItemComponente, Long idItemPai) {
        Item itemComponente = buscarItemPorId(idItemComponente);

        if (itemComponente.getTipoControle() != TipoControleItem.COMPONENTE) {
            throw new RegraNegocioException("O item a ser anexado precisa ser um Componente");
        }

        Item itemPai = buscarItemPorId(idItemPai);

        if (itemPai.getTipoControle() != TipoControleItem.PATRIMONIADO) {
            throw new RegraNegocioException("Este item não aceita componentes");
        }

        ItemComponente componente = ItemComponente.builder()
                .itemPai(itemPai)
                .itemComponente(itemComponente)
                .build();

        itemPai.getVinculosDeComponentes().add(componente);
        itemRepository.save(itemPai);
    }

    private Item buscarItemPorId(Long idItem) {

        if (idItem == null || idItem <= 0) throw new RegraNegocioException("ID do item inválido");

        return itemRepository.findById(idItem)
                .orElseThrow(() -> new RegraNegocioException("Item inexistente"));
    }

    private Local buscarLocalPorId(Long idLocal) {
        if (idLocal == null || idLocal <= 0) throw new RegraNegocioException("ID do local inválido");

        return localRepository.findById(idLocal)
                .orElseThrow(() -> new RegraNegocioException("Local inexistente"));
    }

    private ModeloItem buscarModeloPorId(Long idModelo) {
        if (idModelo == null || idModelo <= 0) {
            throw new RegraNegocioException("ID do modelo inválido");
        }

        return modeloItemRepository.findById(idModelo)
                .orElseThrow(() -> new RegraNegocioException("Modelo inexistente"));
    }

    private void validarTipoRegistroComQuantidade(TipoRegistroItem tipoRegistro, int quantidade) {
        if (tipoRegistro == null) {
            throw new RegraNegocioException("O tipo de registro é obrigatório");
        }

        if (TipoRegistroItem.UNIDADE.equals(tipoRegistro) && quantidade > 1) {
            throw new RegraNegocioException("Unidade só é permitido um por vez");
        }

        if (TipoRegistroItem.LOTE.equals(tipoRegistro) && quantidade <= 1) {
            throw new RegraNegocioException("Em lote a quantidade deve ser maior que 1");
        }
    }


}
