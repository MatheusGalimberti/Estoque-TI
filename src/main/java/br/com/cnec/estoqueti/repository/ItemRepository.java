package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
