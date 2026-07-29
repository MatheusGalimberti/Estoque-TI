package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Categoria;
import br.com.cnec.estoqueti.entity.Item;
import br.com.cnec.estoqueti.entity.ItemComponente;
import br.com.cnec.estoqueti.entity.ModeloItem;
import br.com.cnec.estoqueti.enums.TipoControleItem;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.ItemComponenteRepository;
import br.com.cnec.estoqueti.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemComponenteServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemComponenteRepository itemComponenteRepository;

    @InjectMocks
    private ItemComponenteService itemComponenteService;

    private Item itemPai;
    private Item componente;

    @BeforeEach
    void setUp() {
        itemPai = Item.builder()
                .id(1L)
                .tipoControle(TipoControleItem.PATRIMONIADO)
                .build();

        componente = criarComponente(
                2L,
                "SSD"
        );
    }

    // =========================================================
    // INSTALAR
    // =========================================================

    @Test
    void deveInstalarComponenteNoItemPai() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .existsByItemComponenteAndRemovidoEmIsNull(componente))
                .thenReturn(false);

        when(itemComponenteRepository.save(any(ItemComponente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ItemComponente resultado =
                itemComponenteService.instalar(1L, 2L);

        assertNotNull(resultado);
        assertSame(itemPai, resultado.getItemPai());
        assertSame(componente, resultado.getItemComponente());
        assertNull(resultado.getRemovidoEm());

        verify(itemComponenteRepository)
                .save(any(ItemComponente.class));
    }

    @Test
    void deveConstruirVinculoComItemPaiEComponenteCorretos() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .existsByItemComponenteAndRemovidoEmIsNull(componente))
                .thenReturn(false);

        when(itemComponenteRepository.save(any(ItemComponente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        itemComponenteService.instalar(1L, 2L);

        ArgumentCaptor<ItemComponente> captor =
                ArgumentCaptor.forClass(ItemComponente.class);

        verify(itemComponenteRepository).save(captor.capture());

        ItemComponente vinculoSalvo = captor.getValue();

        assertSame(itemPai, vinculoSalvo.getItemPai());
        assertSame(componente, vinculoSalvo.getItemComponente());
        assertNull(vinculoSalvo.getRemovidoEm());
    }

    @Test
    void deveImpedirInstalacaoQuandoIdDoPaiForNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(null, 2L)
        );

        assertEquals("ID inválido", exception.getMessage());

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemComponenteRepository);
    }

    @Test
    void deveImpedirInstalacaoQuandoIdDoComponenteForInvalido() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 0L)
        );

        assertEquals("ID inválido", exception.getMessage());

        /*
         * Este teste confirma exatamente a tua intenção:
         * nenhum SELECT é executado quando um dos IDs é inválido.
         */
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemComponenteRepository);
    }

    @Test
    void deveImpedirInstalacaoQuandoItemPaiNaoExistir() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 2L)
        );

        assertEquals("Item inexistente", exception.getMessage());

        verify(itemRepository).findById(1L);
        verify(itemRepository, never()).findById(2L);
        verifyNoInteractions(itemComponenteRepository);
    }

    @Test
    void deveImpedirInstalacaoQuandoComponenteNaoExistir() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 2L)
        );

        assertEquals("Item inexistente", exception.getMessage());

        verify(itemComponenteRepository, never())
                .save(any(ItemComponente.class));
    }

    @Test
    void deveImpedirInstalacaoQuandoItemPaiNaoForPatrimoniado() {
        Item paiInvalido = Item.builder()
                .id(1L)
                .tipoControle(TipoControleItem.COMPONENTE)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(paiInvalido));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 2L)
        );

        assertEquals(
                "Este item não pode receber componentes",
                exception.getMessage()
        );

        verify(itemRepository, never()).findById(2L);
        verifyNoInteractions(itemComponenteRepository);
    }

    @Test
    void deveImpedirInstalacaoQuandoItemNaoForComponente() {
        Item itemInvalido = Item.builder()
                .id(2L)
                .tipoControle(TipoControleItem.PATRIMONIADO)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(itemInvalido));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 2L)
        );

        assertEquals(
                "Este item não pode ser componente",
                exception.getMessage()
        );

        verifyNoInteractions(itemComponenteRepository);
    }

    @Test
    void deveImpedirInstalacaoQuandoComponenteJaEstiverInstalado() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .existsByItemComponenteAndRemovidoEmIsNull(componente))
                .thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 2L)
        );

        assertEquals(
                "Este componente já está instalado em outro item",
                exception.getMessage()
        );

        verify(itemComponenteRepository, never())
                .findAllByItemPaiAndRemovidoEmIsNull(any());

        verify(itemComponenteRepository, never())
                .save(any(ItemComponente.class));
    }

    @Test
    void deveInstalarFonteQuandoItemPaiNaoPossuirOutraFonte() {
        Item fonte = criarComponente(2L, "Fonte");

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(fonte));

        when(itemComponenteRepository
                .existsByItemComponenteAndRemovidoEmIsNull(fonte))
                .thenReturn(false);

        when(itemComponenteRepository
                .findAllByItemPaiAndRemovidoEmIsNull(itemPai))
                .thenReturn(List.of());

        when(itemComponenteRepository.save(any(ItemComponente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ItemComponente resultado =
                itemComponenteService.instalar(1L, 2L);

        assertNotNull(resultado);
        assertSame(fonte, resultado.getItemComponente());

        verify(itemComponenteRepository)
                .save(any(ItemComponente.class));
    }

    @Test
    void deveImpedirInstalacaoDeSegundaFonte() {
        Item novaFonte = criarComponente(2L, "Fonte");
        Item fonteInstalada = criarComponente(3L, "Fonte");

        ItemComponente vinculoExistente = ItemComponente.builder()
                .id(10L)
                .itemPai(itemPai)
                .itemComponente(fonteInstalada)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(novaFonte));

        when(itemComponenteRepository
                .existsByItemComponenteAndRemovidoEmIsNull(novaFonte))
                .thenReturn(false);

        when(itemComponenteRepository
                .findAllByItemPaiAndRemovidoEmIsNull(itemPai))
                .thenReturn(List.of(vinculoExistente));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 2L)
        );

        assertEquals(
                "Só é possível ter uma fonte instalada",
                exception.getMessage()
        );

        verify(itemComponenteRepository, never())
                .save(any(ItemComponente.class));
    }

    @Test
    void deveCompararCategoriaFonteSemDiferenciarMaiusculas() {
        Item novaFonte = criarComponente(2L, "FONTE");
        Item fonteInstalada = criarComponente(3L, "fonte");

        ItemComponente vinculoExistente = ItemComponente.builder()
                .itemPai(itemPai)
                .itemComponente(fonteInstalada)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(novaFonte));

        when(itemComponenteRepository
                .existsByItemComponenteAndRemovidoEmIsNull(novaFonte))
                .thenReturn(false);

        when(itemComponenteRepository
                .findAllByItemPaiAndRemovidoEmIsNull(itemPai))
                .thenReturn(List.of(vinculoExistente));

        assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.instalar(1L, 2L)
        );

        verify(itemComponenteRepository, never())
                .save(any(ItemComponente.class));
    }

    // =========================================================
    // REMOVER
    // =========================================================

    @Test
    void deveRemoverComponenteDoItemPai() {
        ItemComponente vinculo = ItemComponente.builder()
                .id(10L)
                .itemPai(itemPai)
                .itemComponente(componente)
                .instaladoEm(LocalDateTime.now().minusDays(5))
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .findByItemPaiAndItemComponenteAndRemovidoEmIsNull(
                        itemPai,
                        componente
                ))
                .thenReturn(Optional.of(vinculo));

        ItemComponente resultado =
                itemComponenteService.remover(1L, 2L);

        assertSame(vinculo, resultado);
        assertNotNull(resultado.getRemovidoEm());

        verify(itemComponenteRepository, never())
                .delete(any(ItemComponente.class));

        verify(itemComponenteRepository, never())
                .save(any(ItemComponente.class));
    }

    @Test
    void deveImpedirRemocaoQuandoNaoExistirVinculoAtivo() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .findByItemPaiAndItemComponenteAndRemovidoEmIsNull(
                        itemPai,
                        componente
                ))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.remover(1L, 2L)
        );

        assertEquals(
                "Nenhum vinculo ativo entre os itens encontrado",
                exception.getMessage()
        );
    }

    @Test
    void deveImpedirRemocaoSemConsultarBancoQuandoSegundoIdForInvalido() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.remover(1L, 0L)
        );

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemComponenteRepository);
    }

    // =========================================================
    // LISTAR COMPONENTES INSTALADOS
    // =========================================================

    @Test
    void deveListarComponentesInstaladosNoItemPai() {
        ItemComponente vinculo = ItemComponente.builder()
                .id(10L)
                .itemPai(itemPai)
                .itemComponente(componente)
                .build();

        List<ItemComponente> vinculos = List.of(vinculo);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemComponenteRepository
                .findAllByItemPaiAndRemovidoEmIsNull(itemPai))
                .thenReturn(vinculos);

        List<ItemComponente> resultado =
                itemComponenteService.listarComponentesInstalados(1L);

        assertEquals(1, resultado.size());
        assertSame(vinculo, resultado.getFirst());
    }

    // =========================================================
    // HISTÓRICO DO ITEM PAI
    // =========================================================

    @Test
    void deveListarHistoricoDeComponentesDoItemPai() {
        ItemComponente vinculoAtual = ItemComponente.builder()
                .id(10L)
                .itemPai(itemPai)
                .itemComponente(componente)
                .build();

        Item outroComponente = criarComponente(3L, "Memória RAM");

        ItemComponente vinculoRemovido = ItemComponente.builder()
                .id(11L)
                .itemPai(itemPai)
                .itemComponente(outroComponente)
                .removidoEm(LocalDateTime.now())
                .build();

        List<ItemComponente> historico =
                List.of(vinculoAtual, vinculoRemovido);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemPai));

        when(itemComponenteRepository.findAllByItemPai(itemPai))
                .thenReturn(historico);

        List<ItemComponente> resultado =
                itemComponenteService
                        .listarHistoricoDoItemPai(1L);

        assertEquals(2, resultado.size());
        assertSame(vinculoAtual, resultado.get(0));
        assertSame(vinculoRemovido, resultado.get(1));
    }

    // =========================================================
    // INSTALAÇÃO ATUAL DO COMPONENTE
    // =========================================================

    @Test
    void deveBuscarInstalacaoAtualDoComponente() {
        ItemComponente vinculo = ItemComponente.builder()
                .id(10L)
                .itemPai(itemPai)
                .itemComponente(componente)
                .build();

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .findByItemComponenteAndRemovidoEmIsNull(componente))
                .thenReturn(Optional.of(vinculo));

        ItemComponente resultado =
                itemComponenteService.buscarInstalacaoAtual(2L);

        assertSame(vinculo, resultado);
        assertSame(itemPai, resultado.getItemPai());
    }

    @Test
    void deveLancarExcecaoQuandoComponenteNaoEstiverInstalado() {
        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .findByItemComponenteAndRemovidoEmIsNull(componente))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemComponenteService.buscarInstalacaoAtual(2L)
        );

        assertEquals(
                "Componente não está instalado",
                exception.getMessage()
        );
    }

    // =========================================================
    // HISTÓRICO DO COMPONENTE
    // =========================================================

    @Test
    void deveListarHistoricoDoComponente() {
        ItemComponente vinculoAntigo = ItemComponente.builder()
                .id(10L)
                .itemPai(itemPai)
                .itemComponente(componente)
                .removidoEm(LocalDateTime.now().minusDays(1))
                .build();

        Item outroPai = Item.builder()
                .id(3L)
                .tipoControle(TipoControleItem.PATRIMONIADO)
                .build();

        ItemComponente vinculoAtual = ItemComponente.builder()
                .id(11L)
                .itemPai(outroPai)
                .itemComponente(componente)
                .build();

        List<ItemComponente> historico =
                List.of(vinculoAntigo, vinculoAtual);

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(componente));

        when(itemComponenteRepository
                .findAllByItemComponente(componente))
                .thenReturn(historico);

        List<ItemComponente> resultado =
                itemComponenteService
                        .listarHistoricoDoComponente(2L);

        assertEquals(2, resultado.size());
        assertSame(vinculoAntigo, resultado.get(0));
        assertSame(vinculoAtual, resultado.get(1));
    }

    // =========================================================
    // MÉTODO AUXILIAR
    // =========================================================

    private Item criarComponente(Long id, String nomeCategoria) {
        Categoria categoria = mock(Categoria.class);
        ModeloItem modelo = mock(ModeloItem.class);

        /*
         * Lenient evita que o Mockito reclame nos testes nos quais
         * a categoria não chega a ser consultada por causa de uma
         * validação anterior.
         */
        lenient().when(categoria.getNome())
                .thenReturn(nomeCategoria);

        lenient().when(modelo.getCategoria())
                .thenReturn(categoria);

        return Item.builder()
                .id(id)
                .tipoControle(TipoControleItem.COMPONENTE)
                .modeloItem(modelo)
                .build();
    }
}