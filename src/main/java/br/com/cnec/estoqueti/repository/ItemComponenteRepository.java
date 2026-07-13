package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.ItemComponente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemComponenteRepository extends JpaRepository<ItemComponente, Long> {


    List<ItemComponente> findByItemPaiIdAndAtivoTrue(Long itemId);

    List<ItemComponente> findByItemComponenteId (Long itemId);

    Optional<ItemComponente> findByItemComponenteIdAndAtivoTrue(
            Long itemComponenteId);
}
