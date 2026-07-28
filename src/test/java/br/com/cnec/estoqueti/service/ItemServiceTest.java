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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ModeloItemRepository modeloItemRepository;

    @Mock
    private LocalRepository localRepository;

    @Mock
    private RegistroCondicaoService registroCondicaoService;

    @InjectMocks
    private ItemService itemService;

    private ModeloItem modelo;
    private Local local;

    @BeforeEach
    void setUp() {
        modelo = ModeloItem.builder()
                .id(1L)
                .build();

        local = Local.builder()
                .id(1L)
                .build();
    }

    // =========================================================
    // CADASTRAR
    // =========================================================

    @Test
    void deveCadastrarItemUnidade() {
        when(modeloItemRepository.findById(1L))
                .thenReturn(Optional.of(modelo));

        when(localRepository.findById(1L))
                .thenReturn(Optional.of(local));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Item resultado = itemService.cadastrar(
                1L,
                1L,
                1,
                TipoControleItem.PATRIMONIADO,
                TipoRegistroItem.UNIDADE,
                "12345",
                "Computador da secretaria",
                StatusItem.DISPONIVEL,
                "ABC123"
        );

        assertNotNull(resultado);
        assertEquals(modelo, resultado.getModeloItem());
        assertEquals(local, resultado.getLocalAtual());
        assertEquals(1, resultado.getQuantidade());
        assertEquals(TipoControleItem.PATRIMONIADO, resultado.getTipoControle());
        assertEquals(TipoRegistroItem.UNIDADE, resultado.getTipoRegistro());
        assertEquals(StatusItem.DISPONIVEL, resultado.getStatusItem());
        assertEquals("12345", resultado.getPatrimonio());
        assertEquals("ABC123", resultado.getNumeroSerie());
        assertEquals("Computador da secretaria", resultado.getObservacoes());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void deveConverterQuantidadeZeroParaUm() {
        when(modeloItemRepository.findById(1L))
                .thenReturn(Optional.of(modelo));

        when(localRepository.findById(1L))
                .thenReturn(Optional.of(local));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Item resultado = itemService.cadastrar(
                1L,
                1L,
                0,
                TipoControleItem.PATRIMONIADO,
                TipoRegistroItem.UNIDADE,
                null,
                null,
                StatusItem.DISPONIVEL,
                null
        );

        assertEquals(1, resultado.getQuantidade());
    }

    @Test
    void deveCadastrarItemEmLote() {
        when(modeloItemRepository.findById(1L))
                .thenReturn(Optional.of(modelo));

        when(localRepository.findById(1L))
                .thenReturn(Optional.of(local));

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Item resultado = itemService.cadastrar(
                1L,
                1L,
                10,
                TipoControleItem.COMPONENTE,
                TipoRegistroItem.LOTE,
                null,
                null,
                StatusItem.DISPONIVEL,
                null
        );

        assertEquals(10, resultado.getQuantidade());
        assertEquals(TipoRegistroItem.LOTE, resultado.getTipoRegistro());
    }

    @Test
    void deveLancarExcecaoQuandoIdModeloForInvalido() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        0L,
                        1L,
                        1,
                        TipoControleItem.PATRIMONIADO,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verifyNoInteractions(modeloItemRepository);
        verifyNoInteractions(localRepository);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveLancarExcecaoQuandoIdLocalForInvalido() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        0L,
                        1,
                        TipoControleItem.PATRIMONIADO,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveLancarExcecaoQuandoTipoControleForNulo() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        1,
                        null,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveLancarExcecaoQuandoStatusForNulo() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        1,
                        TipoControleItem.PATRIMONIADO,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        null,
                        null
                )
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForNegativa() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        -1,
                        TipoControleItem.PATRIMONIADO,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveLancarExcecaoQuandoTipoRegistroForNulo() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        1,
                        TipoControleItem.PATRIMONIADO,
                        null,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveImpedirUnidadeComQuantidadeMaiorQueUm() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        2,
                        TipoControleItem.PATRIMONIADO,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveImpedirLoteComQuantidadeMenorOuIgualAUm() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        1,
                        TipoControleItem.COMPONENTE,
                        TipoRegistroItem.LOTE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveLancarExcecaoQuandoModeloNaoExistir() {
        when(modeloItemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        1,
                        TipoControleItem.PATRIMONIADO,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verify(itemRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoLocalNaoExistir() {
        when(modeloItemRepository.findById(1L))
                .thenReturn(Optional.of(modelo));

        when(localRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () -> itemService.cadastrar(
                        1L,
                        1L,
                        1,
                        TipoControleItem.PATRIMONIADO,
                        TipoRegistroItem.UNIDADE,
                        null,
                        null,
                        StatusItem.DISPONIVEL,
                        null
                )
        );

        verify(itemRepository, never()).save(any());
    }

    // =========================================================
    // MUDAR STATUS
    // =========================================================

    @Test
    void deveMudarStatusDoItem() {
        Item item = Item.builder()
                .id(1L)
                .statusItem(StatusItem.DISPONIVEL)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item resultado = itemService.mudarStatus(
                1L,
                StatusItem.EM_USO
        );

        assertEquals(StatusItem.EM_USO, resultado.getStatusItem());
    }

    @Test
    void deveImpedirMudancaParaStatusNulo() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.mudarStatus(1L, null)
        );

        verifyNoInteractions(itemRepository);
    }

    // =========================================================
    // MOVER ITEM
    // =========================================================

    @Test
    void deveMoverItemParaOutroLocal() {
        Local novoLocal = Local.builder()
                .id(2L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .localAtual(local)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(localRepository.findById(2L))
                .thenReturn(Optional.of(novoLocal));

        Item resultado = itemService.moverItem(
                2L,
                1L
        );

        assertEquals(novoLocal, resultado.getLocalAtual());
        assertEquals(2L, resultado.getLocalAtual().getId());
    }

    // =========================================================
    // ADICIONAR CONDIÇÃO
    // =========================================================

    @Test
    void deveAdicionarCondicaoAoItem() {
        Item item = Item.builder()
                .id(1L)
                .build();

        RegistroCondicao registro = RegistroCondicao.builder()
                .id(10L)
                .item(item)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(registroCondicaoService.cadastrar(
                1L,
                2L,
                "Fonte com problema"
        )).thenReturn(registro);

        itemService.adicionarCondicao(
                1L,
                2L,
                "Fonte com problema"
        );

        verify(registroCondicaoService).cadastrar(
                1L,
                2L,
                "Fonte com problema"
        );
    }

    @Test
    void naoDeveCadastrarCondicaoQuandoItemNaoExistir() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () -> itemService.adicionarCondicao(
                        1L,
                        2L,
                        "Teste"
                )
        );

        verifyNoInteractions(registroCondicaoService);
    }

    // =========================================================
    // COMPONENTES
    // =========================================================

    @Test
    void deveAnexarComponenteAoItemPatrimoniado() {
        Item itemComponente = Item.builder()
                .id(1L)
                .tipoControle(TipoControleItem.COMPONENTE)
                .build();

        Item itemPai = Item.builder()
                .id(2L)
                .tipoControle(TipoControleItem.PATRIMONIADO)
                .vinculosDeComponentes(new ArrayList<>())
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemComponente));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(itemPai));

        when(itemRepository.save(itemPai))
                .thenReturn(itemPai);

        itemService.anexarComponenteAoItemPai(
                1L,
                2L
        );

        assertEquals(1, itemPai.getVinculosDeComponentes().size());

        ItemComponente vinculo =
                itemPai.getVinculosDeComponentes().getFirst();

        assertEquals(itemPai, vinculo.getItemPai());
        assertEquals(itemComponente, vinculo.getItemComponente());

        verify(itemRepository).save(itemPai);
    }

    @Test
    void deveImpedirAnexarItemQueNaoSejaComponente() {
        Item item = Item.builder()
                .id(1L)
                .tipoControle(TipoControleItem.PATRIMONIADO)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemService.anexarComponenteAoItemPai(
                        1L,
                        2L
                )
        );

        assertEquals(
                "O item a ser anexado precisa ser um Componente",
                exception.getMessage()
        );

        verify(itemRepository, never()).findById(2L);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void deveImpedirAnexarComponenteEmItemNaoPatrimoniado() {
        Item itemComponente = Item.builder()
                .id(1L)
                .tipoControle(TipoControleItem.COMPONENTE)
                .build();

        Item itemPai = Item.builder()
                .id(2L)
                .tipoControle(TipoControleItem.COMPONENTE)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(itemComponente));

        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(itemPai));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> itemService.anexarComponenteAoItemPai(
                        1L,
                        2L
                )
        );

        assertEquals(
                "Este item não aceita componentes",
                exception.getMessage()
        );

        verify(itemRepository, never()).save(any());
    }

    // =========================================================
    // BUSCAS / IDS INVÁLIDOS
    // =========================================================

    @Test
    void deveLancarExcecaoQuandoIdItemForInvalido() {
        assertThrows(
                RegraNegocioException.class,
                () -> itemService.moverItem(1L, 0L)
        );

        verifyNoInteractions(itemRepository);
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExistir() {
        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () -> itemService.mudarStatus(
                        99L,
                        StatusItem.EM_USO
                )
        );
    }
}