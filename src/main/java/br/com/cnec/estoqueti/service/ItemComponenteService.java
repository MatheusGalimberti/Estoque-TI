    package br.com.cnec.estoqueti.service;

    import br.com.cnec.estoqueti.entity.Item;
    import br.com.cnec.estoqueti.entity.ItemComponente;
    import br.com.cnec.estoqueti.enums.TipoControleItem;
    import br.com.cnec.estoqueti.exception.RegraNegocioException;
    import br.com.cnec.estoqueti.repository.ItemComponenteRepository;
    import br.com.cnec.estoqueti.repository.ItemRepository;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.util.List;

    @Service
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

            validarId(idItemPai);
            validarId(idItemComponente);

            Item itemPai = validarItemPai(idItemPai);

            Item itemComponente = validarItemComponente(idItemComponente);

            if (itemComponenteRepository
                    .existsByItemComponenteAndRemovidoEmIsNull(itemComponente)) {
                throw new RegraNegocioException(
                        "Este componente já está instalado em outro item"
                );
            }

            String categoriaComponente = itemComponente.getModeloItem().getCategoria().getNome();

            if ("Fonte".equalsIgnoreCase(categoriaComponente)) {
                List<ItemComponente> componentes =
                        itemComponenteRepository
                                .findAllByItemPaiAndRemovidoEmIsNull(itemPai);

                boolean possuiFonte = componentes.stream()
                        .anyMatch(componenteInstalado ->
                                "Fonte".equalsIgnoreCase(
                                        componenteInstalado
                                                .getItemComponente()
                                                .getModeloItem()
                                                .getCategoria()
                                                .getNome()
                                )
                        );

                if (possuiFonte) {
                    throw new RegraNegocioException(
                            "Só é possível ter uma fonte instalada"
                    );
                }
            }


            ItemComponente instalacao = ItemComponente.builder()
                    .itemPai(itemPai)
                    .itemComponente(itemComponente)
                    .build();

            return itemComponenteRepository.save(instalacao);
        }

        @Transactional
        public ItemComponente remover(Long idItemPai, Long idItemComponente) {
            validarId(idItemPai);
            validarId(idItemComponente);

            Item itemPai = validarItemPai(idItemPai);
            Item componente = validarItemComponente(idItemComponente);

            ItemComponente vinculo = itemComponenteRepository
                    .findByItemPaiAndItemComponenteAndRemovidoEmIsNull(
                            itemPai,
                            componente
                    )
                    .orElseThrow(() ->
                            new RegraNegocioException(
                                    "Nenhum vinculo ativo entre os itens encontrado"
                            )
                    );

            vinculo.setRemovidoEm(LocalDateTime.now());

            return vinculo;
        }


        public List<ItemComponente> listarComponentesInstalados(Long idItemPai) {
            Item item = validarItemPai(idItemPai);

            return itemComponenteRepository.findAllByItemPaiAndRemovidoEmIsNull(item);
        }

        public List<ItemComponente> listarHistoricoDoItemPai(Long idItemPai) {
            Item item = validarItemPai(idItemPai);

            return itemComponenteRepository.findAllByItemPai(item);
        }

        // Agora pensando no lado do componente um pouco

        public ItemComponente buscarInstalacaoAtual(Long idComponente) {
            Item componente = validarItemComponente(idComponente);

            return itemComponenteRepository
                    .findByItemComponenteAndRemovidoEmIsNull(componente)
                    .orElseThrow(() ->
                            new RegraNegocioException(
                                    "Componente não está instalado"
                            )
                    );
        }

        public List<ItemComponente> listarHistoricoDoComponente(Long idComponente) {
            Item componente = validarItemComponente(idComponente);

            return itemComponenteRepository.findAllByItemComponente(componente);
        }

        private Item validarItemComponente(Long idItemComponente) {
            validarId(idItemComponente);

            Item item = itemRepository.findById(idItemComponente)
                    .orElseThrow(() -> new RegraNegocioException("Item inexistente"));

            if (item.getTipoControle() != TipoControleItem.COMPONENTE) {
                throw new RegraNegocioException("Este item não pode ser componente");
            }
            return item;
        }

        private Item validarItemPai(Long idItemPai) {

            validarId(idItemPai);

            Item item = itemRepository.findById(idItemPai)
                    .orElseThrow(() -> new RegraNegocioException("Item inexistente"));

            if (item.getTipoControle() != TipoControleItem.PATRIMONIADO) {
                throw new RegraNegocioException("Este item não pode receber componentes");
            }
            return item;
        }

        private void validarId(Long id) {
            if (id == null || id <= 0) {
                throw new RegraNegocioException("ID inválido");
            }
        }


    }
