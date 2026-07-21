    package br.com.cnec.estoqueti.service;

    import br.com.cnec.estoqueti.entity.Categoria;
    import br.com.cnec.estoqueti.exception.RegraNegocioException;
    import br.com.cnec.estoqueti.repository.CategoriaRepository;
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
    class CategoriaServiceTest {

        @Mock
        private CategoriaRepository categoriaRepository;

        @InjectMocks
        private CategoriaService categoriaService;

        private Categoria categoria;

        @BeforeEach
        void setUp() {
            categoria = Categoria.builder()
                    .nome("  Monitor  ")
                    .build();
        }

        @Test
        void deveCadastrarCategoriaComSucesso() {
            when(categoriaRepository.existsByNomeIgnoreCaseAndAtivoTrue("Monitor"))
                    .thenReturn(false);

            when(categoriaRepository.existsByNomeIgnoreCaseAndAtivoFalse("Monitor"))
                    .thenReturn(false);

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Categoria resultado = categoriaService.cadastrar(categoria);

            assertNotNull(resultado);
            assertEquals("Monitor", resultado.getNome());
            assertTrue(resultado.getAtivo());

            verify(categoriaRepository).save(categoria);
        }

        @Test
        void naoDeveCadastrarCategoriaNula() {
            RegraNegocioException exception = assertThrows(
                    RegraNegocioException.class,
                    () -> categoriaService.cadastrar(null)
            );

            assertEquals(
                    "A categoria é obrigatória.",
                    exception.getMessage()
            );

            verifyNoInteractions(categoriaRepository);
        }

        @Test
        void naoDeveCadastrarCategoriaSemNome() {
            categoria.setNome(" ");

            RegraNegocioException exception = assertThrows(
                    RegraNegocioException.class,
                    () -> categoriaService.cadastrar(categoria)
            );

            assertEquals(
                    "O nome da categoria é obrigatório.",
                    exception.getMessage()
            );

            verifyNoInteractions(categoriaRepository);
        }

        @Test
        void naoDeveCadastrarCategoriaAtivaDuplicada() {
            when(categoriaRepository.existsByNomeIgnoreCaseAndAtivoTrue("Monitor"))
                    .thenReturn(true);

            RegraNegocioException exception = assertThrows(
                    RegraNegocioException.class,
                    () -> categoriaService.cadastrar(categoria)
            );

            assertEquals(
                    "Já existe uma categoria ativa com esse nome.",
                    exception.getMessage()
            );

            verify(categoriaRepository, never()).save(any());
            verify(categoriaRepository, never())
                    .existsByNomeIgnoreCaseAndAtivoFalse(anyString());
        }

        @Test
        void naoDeveCadastrarCategoriaDesativadaDuplicada() {
            when(categoriaRepository.existsByNomeIgnoreCaseAndAtivoTrue("Monitor"))
                    .thenReturn(false);

            when(categoriaRepository.existsByNomeIgnoreCaseAndAtivoFalse("Monitor"))
                    .thenReturn(true);

            RegraNegocioException exception = assertThrows(
                    RegraNegocioException.class,
                    () -> categoriaService.cadastrar(categoria)
            );

            assertEquals(
                    "Essa categoria já existe, mas está desativada.",
                    exception.getMessage()
            );

            verify(categoriaRepository, never()).save(any());
        }

        @Test
        void deveListarCategoriasAtivas() {
            Categoria categoria1 = Categoria.builder()
                    .id(1L)
                    .nome("Monitor")
                    .ativo(true)
                    .build();

            Categoria categoria2 = Categoria.builder()
                    .id(2L)
                    .nome("Projetor")
                    .ativo(true)
                    .build();

            when(categoriaRepository.findAllByAtivoTrue())
                    .thenReturn(List.of(categoria1, categoria2));

            List<Categoria> resultado = categoriaService.listarAtivas();

            assertEquals(2, resultado.size());
            assertEquals("Monitor", resultado.get(0).getNome());
            assertEquals("Projetor", resultado.get(1).getNome());

            verify(categoriaRepository).findAllByAtivoTrue();
        }

        @Test
        void deveRetornarListaVaziaQuandoNaoExistiremCategoriasAtivas() {
            when(categoriaRepository.findAllByAtivoTrue())
                    .thenReturn(Collections.emptyList());

            List<Categoria> resultado = categoriaService.listarAtivas();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());

            verify(categoriaRepository).findAllByAtivoTrue();
        }

        @Test
        void deveBuscarCategoriaPorId() {
            Categoria categoriaExistente = Categoria.builder()
                    .id(1L)
                    .nome("Monitor")
                    .ativo(true)
                    .build();

            when(categoriaRepository.findById(1L))
                    .thenReturn(Optional.of(categoriaExistente));

            Categoria resultado = categoriaService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Monitor", resultado.getNome());

            verify(categoriaRepository).findById(1L);
        }

        @Test
        void naoDeveBuscarCategoriaComIdNulo() {
            RegraNegocioException exception = assertThrows(
                    RegraNegocioException.class,
                    () -> categoriaService.buscarPorId(null)
            );

            assertEquals(
                    "O ID da categoria é obrigatório.",
                    exception.getMessage()
            );

            verifyNoInteractions(categoriaRepository);
        }

        @Test
        void naoDeveBuscarCategoriaInexistente() {
            when(categoriaRepository.findById(99L))
                    .thenReturn(Optional.empty());

            RegraNegocioException exception = assertThrows(
                    RegraNegocioException.class,
                    () -> categoriaService.buscarPorId(99L)
            );

            assertEquals(
                    "Categoria não encontrada.",
                    exception.getMessage()
            );

            verify(categoriaRepository).findById(99L);
        }
    }