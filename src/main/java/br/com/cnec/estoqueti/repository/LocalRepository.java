package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalRepository extends JpaRepository<Local, Long> {
    Local findByNome(String nome);

    boolean findByNomeAndAtivoTrue(String nome);
    boolean existsByNomeIgnoreCaseAndAtivoTrue(String nome);
    boolean existsByNomeIgnoreCaseAndAtivoFalse(String nome);
    List<Local> findAllByAtivoTrue();

}