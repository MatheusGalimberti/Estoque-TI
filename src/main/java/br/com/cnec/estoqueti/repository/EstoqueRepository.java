package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

}
