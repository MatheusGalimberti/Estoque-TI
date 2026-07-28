package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Item;
import br.com.cnec.estoqueti.entity.ItemComponente;
import br.com.cnec.estoqueti.enums.TipoControleItem;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.ItemComponenteRepository;
import br.com.cnec.estoqueti.repository.ItemRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ItemComponenteService {

    private final ItemRepository itemRepository;
    private final ItemComponenteRepository itemComponenteRepository;

    public ItemComponenteService(ItemRepository itemRepository,
                                 ItemComponenteRepository itemComponenteRepository) {
        this.itemRepository = itemRepository;
        this.itemComponenteRepository = itemComponenteRepository;
    }

    @Transactional
    public ItemComponente instalar(Long idItemPai, Long idItemComponente) {

        if (idItemPai == null || idItemPai <= 0) throw new RegraNegocioException("ID invalido");
        if (idItemComponente == null || idItemComponente <= 0) throw new RegraNegocioException("ID invalido");

        Item itemPai = validarItemPai(idItemPai);

        Item itemComponente = validarItemComponente(idItemComponente);

        String categoriaComponente = itemComponente.getModeloItem().getCategoria().getNome();

        if(categoriaComponente.equals("Fonte")){
            List<ItemComponente> componentes = listarComponentesInstalados(idItemPai);

            if(componentes.stream().anyMatch((c) -> "Fonte".equals(c.getItemComponente()
                    .getModeloItem()
                    .getCategoria()
                    .getNome())) == true){
                throw new RegraNegocioException("Só é possivel ter uma vonte no pc");
            }
        }

        ItemComponente historicoDeAnexo = ItemComponente.builder()
                .itemPai(itemPai)
                .itemComponente(itemComponente)
                .build();

        return itemComponenteRepository.save(historicoDeAnexo);
    }

    private List<ItemComponente> listarComponentesInstalados(Long idItemPai){

        Item item = validarItemPai(idItemPai);

        return itemComponenteRepository.findAllByItemPaiAndRemovidoEmIsNull(item);
    }


    private Item validarItemComponente(Long idItemComponente) {
        if (idItemComponente == null || idItemComponente <= 0) throw new RegraNegocioException("ID invalido");

        Item item = itemRepository.findById(idItemComponente)
                    .orElseThrow(() -> new RegraNegocioException("Item inexistente"));

        if(item.getTipoControle() != TipoControleItem.COMPONENTE){
            throw new RegraNegocioException("Este item não pode ser componente");
        }
        return item;
    }

    private Item validarItemPai(Long idItemPai) {

        if (idItemPai == null || idItemPai <= 0) throw new RegraNegocioException("ID invalido");

        Item item = itemRepository.findById(idItemPai)
                .orElseThrow(() -> new RegraNegocioException("Item inexistente"));

        if (item.getTipoControle() != TipoControleItem.PATRIMONIADO) {
            throw new RegraNegocioException("Este item não pode receber componentes");
        }
        return item;
    }





}
