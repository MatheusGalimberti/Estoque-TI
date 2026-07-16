package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Local;
import br.com.cnec.estoqueti.enums.TipoLocal;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.LocalRepository;
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
class LocalServiceTest {

    @Mock
    private LocalRepository localRepository;

    @InjectMocks
    private LocalService localService;

    private Local local;

    @BeforeEach
    void setUp() {
        local = Local.builder()
                .nome("  TI  ")
                .tipo(TipoLocal.ESTOQUE)
                .build();
    }

    @Test
    void deveCadastrarLocalComSucesso() {
        when(localRepository.existsByNomeIgnoreCaseAndAtivoTrue("TI"))
                .thenReturn(false);

        when(localRepository.existsByNomeIgnoreCaseAndAtivoFalse("TI"))
                .thenReturn(false);

        when(localRepository.save(any(Local.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Local resultado = localService.cadastrar(
                local,
                TipoLocal.ESTOQUE
        );

        assertNotNull(resultado);
        assertEquals("TI", resultado.getNome());
        assertEquals(TipoLocal.ESTOQUE, resultado.getTipo());
        assertTrue(resultado.getAtivo());

        verify(localRepository).save(local);
    }

    @Test
    void naoDeveCadastrarLocalNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> localService.cadastrar(
                        null,
                        TipoLocal.ESTOQUE
                )
        );

        assertEquals(
                "Local invalido",
                exception.getMessage()
        );

        verifyNoInteractions(localRepository);
    }

    @Test
    void naoDeveCadastrarSemTipoLocal() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> localService.cadastrar(local, null)
        );

        assertEquals(
                "Tipo de local deve estar presente",
                exception.getMessage()
        );

        verifyNoInteractions(localRepository);
    }

    @Test
    void naoDeveCadastrarLocalSemNome() {
        local.setNome(" ");

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> localService.cadastrar(
                        local,
                        TipoLocal.ESTOQUE
                )
        );

        assertEquals(
                "Nome do local invalido",
                exception.getMessage()
        );

        verifyNoInteractions(localRepository);
    }

    @Test
    void naoDeveCadastrarLocalAtivoDuplicado() {
        when(localRepository.existsByNomeIgnoreCaseAndAtivoTrue("TI"))
                .thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> localService.cadastrar(
                        local,
                        TipoLocal.ESTOQUE
                )
        );

        assertEquals(
                "Já existe um local ativo com esse nome.",
                exception.getMessage()
        );

        verify(localRepository, never()).save(any());
        verify(localRepository, never())
                .existsByNomeIgnoreCaseAndAtivoFalse(anyString());
    }

    @Test
    void naoDeveCadastrarLocalDesativadoDuplicado() {
        when(localRepository.existsByNomeIgnoreCaseAndAtivoTrue("TI"))
                .thenReturn(false);

        when(localRepository.existsByNomeIgnoreCaseAndAtivoFalse("TI"))
                .thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> localService.cadastrar(
                        local,
                        TipoLocal.ESTOQUE
                )
        );

        assertEquals(
                "Esse local já existe, mas está desativado.",
                exception.getMessage()
        );

        verify(localRepository, never()).save(any());
    }

    @Test
    void deveListarLocaisAtivos() {
        Local estoqueTi = Local.builder()
                .id(1L)
                .nome("TI")
                .tipo(TipoLocal.ESTOQUE)
                .ativo(true)
                .build();

        Local laboratorio = Local.builder()
                .id(2L)
                .nome("Laboratório 1")
                .tipo(TipoLocal.LABORATORIO)
                .ativo(true)
                .build();

        when(localRepository.findAllByAtivoTrue())
                .thenReturn(List.of(estoqueTi, laboratorio));

        List<Local> resultado =
                localService.listarLocaisAtivos();

        assertEquals(2, resultado.size());
        assertEquals("TI", resultado.get(0).getNome());
        assertEquals(
                "Laboratório 1",
                resultado.get(1).getNome()
        );

        verify(localRepository).findAllByAtivoTrue();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremLocaisAtivos() {
        when(localRepository.findAllByAtivoTrue())
                .thenReturn(Collections.emptyList());

        List<Local> resultado =
                localService.listarLocaisAtivos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(localRepository).findAllByAtivoTrue();
    }

    @Test
    void deveBuscarLocalPorId() {
        Local localExistente = Local.builder()
                .id(1L)
                .nome("TI")
                .tipo(TipoLocal.ESTOQUE)
                .ativo(true)
                .build();

        when(localRepository.findById(1L))
                .thenReturn(Optional.of(localExistente));

        Local resultado = localService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("TI", resultado.getNome());

        verify(localRepository).findById(1L);
    }

    @Test
    void naoDeveBuscarLocalComIdNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> localService.buscarPorId(null)
        );

        assertEquals(
                "O ID do local é obrigatório.",
                exception.getMessage()
        );

        verifyNoInteractions(localRepository);
    }

    @Test
    void naoDeveBuscarLocalInexistente() {
        when(localRepository.findById(99L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> localService.buscarPorId(99L)
        );

        assertEquals(
                "Local não encontrado.",
                exception.getMessage()
        );

        verify(localRepository).findById(99L);
    }
}