package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Condicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CondicaoRepository extends JpaRepository<Condicao, Long> {
}
