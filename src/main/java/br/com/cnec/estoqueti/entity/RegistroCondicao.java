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
@Table(name = "registros_condicao")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistroCondicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "condicao_id", nullable = false)
    private Condicao condicao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "iniciada_em", nullable = false)
    private LocalDateTime iniciadaEm;

    @Column(name = "resolvida_em")
    private LocalDateTime resolvidaEm;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativa = true;

    @PrePersist
    private void prePersist() {
        if (iniciadaEm == null) {
            iniciadaEm = LocalDateTime.now();
        }

        if (ativa == null) {
            ativa = true;
        }
    }

}
