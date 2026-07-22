package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.RegistroCondicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistroCondicaoRepository
        extends JpaRepository<RegistroCondicao, Long> {

    List<RegistroCondicao> findAllByItem_Id(Long itemId);

    List<RegistroCondicao>
    findAllByItem_IdAndResolvidaEmIsNull(Long itemId);

    Optional<RegistroCondicao> findByIdAndItem_Id(
            Long registroId,
            Long itemId
    );

    boolean existsByItem_IdAndCondicao_IdAndResolvidaEmIsNull(
            Long itemId,
            Long condicaoId
    );
}