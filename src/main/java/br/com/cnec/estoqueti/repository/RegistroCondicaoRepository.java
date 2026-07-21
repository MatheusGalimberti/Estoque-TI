package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.RegistroCondicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroCondicaoRepository extends JpaRepository<RegistroCondicao, Long> {

    List<RegistroCondicao> findAllByItemIdAndResolvidaEmIsNull(Long idItem);

    List<RegistroCondicao> findAllByItemIdOrderByIniciadaEmDesc(Long idItem);
}
