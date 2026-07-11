package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.ModeloItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloItemRepository extends JpaRepository<ModeloItem, Long> {
}
