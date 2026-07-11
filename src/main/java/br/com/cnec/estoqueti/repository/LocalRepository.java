package br.com.cnec.estoqueti.repository;

import br.com.cnec.estoqueti.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalRepository extends JpaRepository<Local, Long> {
}