package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {

    boolean existsByNomeIgnoreCaseAndAtivoTrue(String nome);

    boolean existsByNomeIgnoreCaseAndAtivoFalse(String nome);

    List<Categoria> findAllByAtivoTrue();
}