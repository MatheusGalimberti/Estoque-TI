package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Item;
import br.com.cnec.estoqueti.entity.ItemComponente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemComponenteRepository extends JpaRepository<ItemComponente, Long> {

    List<ItemComponente> findByItemComponenteId (Long itemId);

    List<ItemComponente> findAllByItemPaiAndRemovidoEmIsNull(Item itemPai);

    List<ItemComponente> findAllByItemPai(Item itemPai);

    Optional<ItemComponente> findByItemComponenteAndRemovidoEmIsNull(Item componente);

    List<ItemComponente> findAllByItemComponente(Item componente);

    Optional<ItemComponente> findByItemPaiAndItemComponenteAndRemovidoEmIsNull(Item itemPai, Item itemComponente);

    boolean existsByItemComponenteAndRemovidoEmIsNull(Item itemComponente);
}
