package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Condicao;
import br.com.cnec.estoqueti.entity.Item;
import br.com.cnec.estoqueti.entity.RegistroCondicao;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.CondicaoRepository;
import br.com.cnec.estoqueti.repository.ItemRepository;
import br.com.cnec.estoqueti.repository.RegistroCondicaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class RegistroCondicaoServiceTest {

    @Mock
    private RegistroCondicaoRepository registroCondicaoRepository;

    @Mock
    private CondicaoRepository condicaoRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RegistroCondicaoService registroCondicaoService;

    private Item item;
    private Condicao condicao;
    private RegistroCondicao registro;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .build();

        condicao = Condicao.builder()
                .id(2L)
                .nome("Fonte com defeito")
                .ativo(true)
                .build();

        registro = RegistroCondicao.builder()
                .id(3L)
                .item(item)
                .condicao(condicao)
                .observacao("Fonte não liga")
                .iniciadaEm(LocalDateTime.now())
                .build();
    }

    @Test
    void deveCadastrarRegistroCondicao() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(condicaoRepository.findById(2L))
                .thenReturn(Optional.of(condicao));

        when(registroCondicaoRepository
                .existsByItem_IdAndCondicao_IdAndResolvidaEmIsNull(1L, 2L))
                .thenReturn(false);

        when(registroCondicaoRepository.save(any(RegistroCondicao.class)))
                .thenAnswer(invocation -> {
                    RegistroCondicao registroSalvo = invocation.getArgument(0);
                    registroSalvo.setId(3L);
                    return registroSalvo;
                });

        RegistroCondicao resultado = registroCondicaoService.cadastrar(
                1L,
                2L,
                "  Fonte não liga  "
        );

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals(item, resultado.getItem());
        assertEquals(condicao, resultado.getCondicao());
        assertEquals("Fonte não liga", resultado.getObservacao());
        assertNotNull(resultado.getIniciadaEm());
        assertNull(resultado.getResolvidaEm());

        verify(itemRepository).findById(1L);
        verify(condicaoRepository).findById(2L);

        verify(registroCondicaoRepository)
                .existsByItem_IdAndCondicao_IdAndResolvidaEmIsNull(1L, 2L);

        verify(registroCondicaoRepository)
                .save(any(RegistroCondicao.class));
    }


    @Test
    void deveCadastrarRegistroComObservacaoNull(){
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(condicaoRepository.findById(2L))
                .thenReturn(Optional.of(condicao));


        when(registroCondicaoRepository
                .existsByItem_IdAndCondicao_IdAndResolvidaEmIsNull(1L,2L))
                .thenReturn(false);

        when(registroCondicaoRepository.save(any(RegistroCondicao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistroCondicao resultado = registroCondicaoService.cadastrar(1L,2L,null);

        assertNull(resultado.getObservacao());

        verify(registroCondicaoRepository)
                .save(any(RegistroCondicao.class));

    }
    @Test
    void deveCadastrarRegistroComObservacaoVaziaComoNull() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(condicaoRepository.findById(2L))
                .thenReturn(Optional.of(condicao));


        when(registroCondicaoRepository
                .existsByItem_IdAndCondicao_IdAndResolvidaEmIsNull(1L, 2L))
                .thenReturn(false);

        when(registroCondicaoRepository.save(any(RegistroCondicao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistroCondicao resultado = registroCondicaoService.cadastrar(1L, 2L, "         ");

        assertNull(resultado.getObservacao());
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoForEncontrado() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registroCondicaoService.cadastrar(
                        1L,
                        2L,
                        "Teste"
                )
        );

        assertEquals("Item não encontrado.", exception.getMessage());

        verify(itemRepository).findById(1L);
        verifyNoInteractions(condicaoRepository);
        verifyNoInteractions(registroCondicaoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoCondicaoNaoForEncontrada() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(condicaoRepository.findById(2L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registroCondicaoService.cadastrar(
                        1L,
                        2L,
                        null
                )
        );

        assertEquals("Condição não encontrada.", exception.getMessage());

        verify(itemRepository).findById(1L);
        verify(condicaoRepository).findById(2L);
        verifyNoInteractions(registroCondicaoRepository);
    }

    @Test
    void deveImpedirCadastroComCondicaoDesativada() {
        condicao.setAtivo(false);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(condicaoRepository.findById(2L))
                .thenReturn(Optional.of(condicao));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registroCondicaoService.cadastrar(
                        1L,
                        2L,
                        null
                )
        );

        assertEquals(
                "Não é possível utilizar uma condição desativada.",
                exception.getMessage()
        );

        verify(registroCondicaoRepository, never())
                .save(any(RegistroCondicao.class));
    }

    @Test
    void deveImpedirCondicaoPendenteDuplicada() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(condicaoRepository.findById(2L))
                .thenReturn(Optional.of(condicao));

        when(registroCondicaoRepository
                .existsByItem_IdAndCondicao_IdAndResolvidaEmIsNull(1L, 2L))
                .thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registroCondicaoService.cadastrar(
                        1L,
                        2L,
                        null
                )
        );

        assertEquals(
                "Essa condição já está pendente para o item.",
                exception.getMessage()
        );

        verify(registroCondicaoRepository, never())
                .save(any(RegistroCondicao.class));
    }

    @Test
    void deveListarHistoricoDeCondicoesDoItem() {
        RegistroCondicao registroResolvido = RegistroCondicao.builder()
                .id(4L)
                .item(item)
                .condicao(condicao)
                .iniciadaEm(LocalDateTime.now().minusDays(2))
                .resolvidaEm(LocalDateTime.now().minusDays(1))
                .build();

        List<RegistroCondicao> historico =
                List.of(registro, registroResolvido);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(registroCondicaoRepository.findAllByItem_Id(1L))
                .thenReturn(historico);

        List<RegistroCondicao> resultado =
                registroCondicaoService.listarHistoricoCondicoes(1L);

        assertEquals(2, resultado.size());
        assertEquals(historico, resultado);

        verify(itemRepository).findById(1L);
        verify(registroCondicaoRepository).findAllByItem_Id(1L);
    }

    @Test
    void deveListarCondicoesPendentesDoItem() {
        List<RegistroCondicao> pendentes = List.of(registro);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(registroCondicaoRepository
                .findAllByItem_IdAndResolvidaEmIsNull(1L))
                .thenReturn(pendentes);

        List<RegistroCondicao> resultado =
                registroCondicaoService.listarCondicoesPendentes(1L);

        assertEquals(1, resultado.size());
        assertNull(resultado.getFirst().getResolvidaEm());

        verify(registroCondicaoRepository)
                .findAllByItem_IdAndResolvidaEmIsNull(1L);
    }

    @Test
    void deveEditarObservacaoDeRegistroPendente() {
        when(registroCondicaoRepository
                .findByIdAndItem_Id(3L, 1L))
                .thenReturn(Optional.of(registro));

        when(registroCondicaoRepository.save(registro))
                .thenReturn(registro);

        RegistroCondicao resultado =
                registroCondicaoService.editarObservacao(
                        1L,
                        3L,
                        "  Fonte está fazendo ruído  "
                );

        assertEquals(
                "Fonte está fazendo ruído",
                resultado.getObservacao()
        );

        verify(registroCondicaoRepository).save(registro);
    }

    @Test
    void deveImpedirEdicaoComObservacaoVazia() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registroCondicaoService.editarObservacao(
                        1L,
                        3L,
                        "   "
                )
        );

        assertEquals(
                "A observação não pode ser vazia.",
                exception.getMessage()
        );

        verifyNoInteractions(registroCondicaoRepository);
    }

    @Test
    void deveResolverCondicaoPendente() {
        assertNull(registro.getResolvidaEm());

        when(registroCondicaoRepository
                .findByIdAndItem_Id(3L, 1L))
                .thenReturn(Optional.of(registro));

        when(registroCondicaoRepository.save(registro))
                .thenReturn(registro);

        RegistroCondicao resultado =
                registroCondicaoService.resolverCondicao(1L, 3L);

        assertNotNull(resultado.getResolvidaEm());

        verify(registroCondicaoRepository).save(registro);
    }

    @Test
    void deveImpedirResolverCondicaoJaResolvida() {
        registro.setResolvidaEm(LocalDateTime.now().minusDays(1));

        when(registroCondicaoRepository
                .findByIdAndItem_Id(3L, 1L))
                .thenReturn(Optional.of(registro));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registroCondicaoService.resolverCondicao(1L, 3L)
        );

        assertEquals(
                "A condição já foi resolvida.",
                exception.getMessage()
        );

        verify(registroCondicaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoRegistroNaoPertencerAoItem() {
        when(registroCondicaoRepository
                .findByIdAndItem_Id(3L, 1L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registroCondicaoService.resolverCondicao(1L, 3L)
        );

        assertEquals(
                "Registro de condição não encontrado para esse item.",
                exception.getMessage()
        );

        verify(registroCondicaoRepository, never()).save(any());
    }


}
