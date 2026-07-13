package br.com.cnec.estoqueti.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_componentes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemComponente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_pai_id", nullable = false)
    private Item itemPai;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_componente_id", nullable = false)
    private Item itemComponente;

    @Column(name = "instalado_em", nullable = false)
    private LocalDateTime instaladoEm;

    @Column(name = "removido_em")
    private LocalDateTime removidoEm;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @PrePersist
    private void prePersist() {
        if (instaladoEm == null) {
            instaladoEm = LocalDateTime.now();
        }

        if (ativo == null) {
            ativo = true;
        }
    }
}