package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Condicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CondicaoRepository extends JpaRepository<Condicao, Long> {

    boolean existsByNomeIgnoreCaseAndAtivoTrue(String nome);

    boolean existsByNomeIgnoreCaseAndAtivoFalse(String nome);

    List<Condicao> findAllByAtivoTrue();

    List<Condicao> findAllByAtivoFalse();
}