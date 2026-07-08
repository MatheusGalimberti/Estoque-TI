package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
