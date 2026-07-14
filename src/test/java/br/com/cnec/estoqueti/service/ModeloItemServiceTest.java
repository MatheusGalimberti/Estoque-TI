package br.com.cnec.estoqueti.service;
import br.com.cnec.estoqueti.entity.Categoria;
import br.com.cnec.estoqueti.entity.ModeloItem;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.CategoriaRepository;
import br.com.cnec.estoqueti.repository.ModeloItemRepository;
import br.com.cnec.estoqueti.service.ModeloItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ModeloItemServiceTest {

    @Mock
    private ModeloItemRepository modeloItemRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ModeloItemService modeloItemService;

    private Categoria categoria;
    private ModeloItem modeloItem;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Memória RAM")
                .ativo(true)
                .build();

        modeloItem = ModeloItem.builder()
                .nome("  Memória RAM DDR3 4 GB  ")
                .categoria(categoria)
                .build();
    }

    @Test
    void deveCadastrarModeloItemComSucesso() {
        when(modeloItemRepository.existsByNomeIgnoreCaseAndAtivoTrue(
                "Memória RAM DDR3 4 GB"
        )).thenReturn(false);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(modeloItemRepository.save(any(ModeloItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ModeloItem resultado = modeloItemService.cadastrar(modeloItem);

        assertNotNull(resultado);
        assertEquals("Memória RAM DDR3 4 GB", resultado.getNome());
        assertEquals(categoria, resultado.getCategoria());
        assertTrue(resultado.getAtivo());

        verify(modeloItemRepository).save(modeloItem);
    }

    @Test
    void naoDeveCadastrarModeloComNomeDuplicado() {
        when(modeloItemRepository.existsByNomeIgnoreCaseAndAtivoTrue(
                "Memória RAM DDR3 4 GB"
        )).thenReturn(true);

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> modeloItemService.cadastrar(modeloItem)
        );

        assertEquals(
                "Já existe um modelo de item ativo com esse nome.",
                exception.getMessage()
        );

        verify(modeloItemRepository, never()).save(any());
        verify(categoriaRepository, never()).findById(anyLong());
    }

    @Test
    void naoDeveCadastrarModeloSemNome() {
        modeloItem.setNome(" ");

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> modeloItemService.cadastrar(modeloItem)
        );

        assertEquals(
                "O nome do modelo é obrigatório.",
                exception.getMessage()
        );

        verifyNoInteractions(modeloItemRepository);
        verifyNoInteractions(categoriaRepository);
    }

    @Test
    void naoDeveCadastrarModeloComCategoriaInexistente() {
        when(modeloItemRepository.existsByNomeIgnoreCaseAndAtivoTrue(
                "Memória RAM DDR3 4 GB"
        )).thenReturn(false);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> modeloItemService.cadastrar(modeloItem)
        );

        assertEquals("Categoria não encontrada.", exception.getMessage());

        verify(modeloItemRepository, never()).save(any());
    }
}