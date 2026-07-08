package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.CondicaoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CondicaoItemRepository extends JpaRepository<CondicaoItem, Long> {
}
