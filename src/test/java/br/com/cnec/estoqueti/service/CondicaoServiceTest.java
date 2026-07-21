package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Condicao;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.CondicaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CondicaoServiceTest {

    @Mock
    private CondicaoRepository condicaoRepository;

    @InjectMocks
    private CondicaoService condicaoService;

    private Condicao condicao;

    @BeforeEach
    void setUp() {
        condicao = Condicao.builder()
                .nome("  Fonte com defeito  ")
                .descricao("Fonte não está fornecendo energia.")
                .build();
    }

    @Test
    void deveCadastrarCondicaoComSucesso() {
        when(condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoTrue("Fonte com defeito"))
                .thenReturn(false);

        when(condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoFalse("Fonte com defeito"))
                .thenReturn(false);

        when(condicaoRepository.save(any(Condicao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Condicao resultado = condicaoService.cadastrar(condicao);

        assertNotNull(resultado);
        assertEquals("Fonte com defeito", resultado.getNome());
        assertTrue(resultado.getAtivo());

        verify(condicaoRepository)
                .existsByNomeIgnoreCaseAndAtivoTrue("Fonte com defeito");

        verify(condicaoRepository)
                .existsByNomeIgnoreCaseAndAtivoFalse("Fonte com defeito");

        verify(condicaoRepository).save(condicao);
    }

    @Test
    void deveRemoverIdInformadoAoCadastrarCondicao() {
        condicao.setId(10L);

        when(condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoTrue("Fonte com defeito"))
                .thenReturn(false);

        when(condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoFalse("Fonte com defeito"))
                .thenReturn(false);

        when(condicaoRepository.save(any(Condicao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Condicao resultado = condicaoService.cadastrar(condicao);

        assertNotNull(resultado);
        assertNull(resultado.getId());

        verify(condicaoRepository).save(condicao);
    }

    @Test
    void naoDeveCadastrarCondicaoNula() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.cadastrar(null)
        );

        assertEquals(
                "A condição é obrigatória.",
                exception.getMessage()
        );

        verifyNoInteractions(condicaoRepository);
    }

    @Test
    void naoDeveCadastrarCondicaoComNomeNulo() {
        condicao.setNome(null);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.cadastrar(condicao)
        );

        assertEquals(
                "O nome da condição é obrigatório.",
                exception.getMessage()
        );

        verifyNoInteractions(condicaoRepository);
    }

    @Test
    void naoDeveCadastrarCondicaoComNomeVazio() {
        condicao.setNome("   ");

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.cadastrar(condicao)
        );

        assertEquals(
                "O nome da condição é obrigatório.",
                exception.getMessage()
        );

        verifyNoInteractions(condicaoRepository);
    }

    @Test
    void naoDeveCadastrarCondicaoAtivaDuplicada() {
        when(condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoTrue("Fonte com defeito"))
                .thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.cadastrar(condicao)
        );

        assertEquals(
                "Já existe uma condição ativa com esse nome.",
                exception.getMessage()
        );

        verify(condicaoRepository, never()).save(any());

        verify(condicaoRepository, never())
                .existsByNomeIgnoreCaseAndAtivoFalse(anyString());
    }

    @Test
    void naoDeveCadastrarCondicaoDesativadaDuplicada() {
        when(condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoTrue("Fonte com defeito"))
                .thenReturn(false);

        when(condicaoRepository
                .existsByNomeIgnoreCaseAndAtivoFalse("Fonte com defeito"))
                .thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.cadastrar(condicao)
        );

        assertEquals(
                "Essa condição já existe, mas está desativada.",
                exception.getMessage()
        );

        verify(condicaoRepository, never()).save(any());
    }

    @Test
    void deveListarTodasAsCondicoes() {
        Condicao condicaoAtiva = criarCondicao(
                1L,
                "Fonte com defeito",
                true
        );

        Condicao condicaoDesativada = criarCondicao(
                2L,
                "Condição antiga",
                false
        );

        when(condicaoRepository.findAll())
                .thenReturn(List.of(condicaoAtiva, condicaoDesativada));

        List<Condicao> resultado = condicaoService.listarTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Fonte com defeito", resultado.get(0).getNome());
        assertEquals("Condição antiga", resultado.get(1).getNome());

        verify(condicaoRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCondicoes() {
        when(condicaoRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Condicao> resultado = condicaoService.listarTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(condicaoRepository).findAll();
    }

    @Test
    void deveListarCondicoesAtivas() {
        Condicao condicao1 = criarCondicao(
                1L,
                "Fonte com defeito",
                true
        );

        Condicao condicao2 = criarCondicao(
                2L,
                "SSD com defeito",
                true
        );

        when(condicaoRepository.findAllByAtivoTrue())
                .thenReturn(List.of(condicao1, condicao2));

        List<Condicao> resultado = condicaoService.listarAtivas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getAtivo());
        assertTrue(resultado.get(1).getAtivo());

        verify(condicaoRepository).findAllByAtivoTrue();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCondicoesAtivas() {
        when(condicaoRepository.findAllByAtivoTrue())
                .thenReturn(Collections.emptyList());

        List<Condicao> resultado = condicaoService.listarAtivas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(condicaoRepository).findAllByAtivoTrue();
    }

    @Test
    void deveListarCondicoesDesativadas() {
        Condicao condicao1 = criarCondicao(
                1L,
                "Defeito antigo",
                false
        );

        Condicao condicao2 = criarCondicao(
                2L,
                "Condição descontinuada",
                false
        );

        when(condicaoRepository.findAllByAtivoFalse())
                .thenReturn(List.of(condicao1, condicao2));

        List<Condicao> resultado = condicaoService.listarDesativadas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertFalse(resultado.get(0).getAtivo());
        assertFalse(resultado.get(1).getAtivo());

        verify(condicaoRepository).findAllByAtivoFalse();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCondicoesDesativadas() {
        when(condicaoRepository.findAllByAtivoFalse())
                .thenReturn(Collections.emptyList());

        List<Condicao> resultado = condicaoService.listarDesativadas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(condicaoRepository).findAllByAtivoFalse();
    }

    @Test
    void deveBuscarCondicaoPorId() {
        Condicao condicaoExistente = criarCondicao(
                1L,
                "Fonte com defeito",
                true
        );

        when(condicaoRepository.findById(1L))
                .thenReturn(Optional.of(condicaoExistente));

        Condicao resultado = condicaoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Fonte com defeito", resultado.getNome());

        verify(condicaoRepository).findById(1L);
    }

    @Test
    void naoDeveBuscarCondicaoComIdNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.buscarPorId(null)
        );

        assertEquals(
                "O ID da condição é inválido.",
                exception.getMessage()
        );

        verifyNoInteractions(condicaoRepository);
    }

    @Test
    void naoDeveBuscarCondicaoComIdZero() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.buscarPorId(0L)
        );

        assertEquals(
                "O ID da condição é inválido.",
                exception.getMessage()
        );

        verifyNoInteractions(condicaoRepository);
    }

    @Test
    void naoDeveBuscarCondicaoComIdNegativo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.buscarPorId(-1L)
        );

        assertEquals(
                "O ID da condição é inválido.",
                exception.getMessage()
        );

        verifyNoInteractions(condicaoRepository);
    }

    @Test
    void naoDeveBuscarCondicaoInexistente() {
        when(condicaoRepository.findById(99L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.buscarPorId(99L)
        );

        assertEquals(
                "Condição não encontrada.",
                exception.getMessage()
        );

        verify(condicaoRepository).findById(99L);
    }

    @Test
    void deveAtivarCondicaoComSucesso() {
        Condicao condicaoDesativada = criarCondicao(
                1L,
                "Fonte com defeito",
                false
        );

        when(condicaoRepository.findById(1L))
                .thenReturn(Optional.of(condicaoDesativada));

        when(condicaoRepository.save(condicaoDesativada))
                .thenReturn(condicaoDesativada);

        Condicao resultado = condicaoService.ativarCondicao(1L);

        assertNotNull(resultado);
        assertTrue(resultado.getAtivo());

        verify(condicaoRepository).findById(1L);
        verify(condicaoRepository).save(condicaoDesativada);
    }

    @Test
    void naoDeveAtivarCondicaoQueJaEstaAtiva() {
        Condicao condicaoAtiva = criarCondicao(
                1L,
                "Fonte com defeito",
                true
        );

        when(condicaoRepository.findById(1L))
                .thenReturn(Optional.of(condicaoAtiva));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.ativarCondicao(1L)
        );

        assertEquals(
                "A condição já está ativa.",
                exception.getMessage()
        );

        verify(condicaoRepository).findById(1L);
        verify(condicaoRepository, never()).save(any());
    }

    @Test
    void deveDesativarCondicaoComSucesso() {
        Condicao condicaoAtiva = criarCondicao(
                1L,
                "Fonte com defeito",
                true
        );

        when(condicaoRepository.findById(1L))
                .thenReturn(Optional.of(condicaoAtiva));

        when(condicaoRepository.save(condicaoAtiva))
                .thenReturn(condicaoAtiva);

        Condicao resultado = condicaoService.desativarCondicao(1L);

        assertNotNull(resultado);
        assertFalse(resultado.getAtivo());

        verify(condicaoRepository).findById(1L);
        verify(condicaoRepository).save(condicaoAtiva);
    }

    @Test
    void naoDeveDesativarCondicaoQueJaEstaDesativada() {
        Condicao condicaoDesativada = criarCondicao(
                1L,
                "Fonte com defeito",
                false
        );

        when(condicaoRepository.findById(1L))
                .thenReturn(Optional.of(condicaoDesativada));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> condicaoService.desativarCondicao(1L)
        );

        assertEquals(
                "A condição já está desativada.",
                exception.getMessage()
        );

        verify(condicaoRepository).findById(1L);
        verify(condicaoRepository, never()).save(any());
    }

    private Condicao criarCondicao(
            Long id,
            String nome,
            Boolean ativo
    ) {
        return Condicao.builder()
                .id(id)
                .nome(nome)
                .ativo(ativo)
                .build();
    }
}