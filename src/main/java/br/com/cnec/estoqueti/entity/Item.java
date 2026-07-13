package br.com.cnec.estoqueti.entity;

import br.com.cnec.estoqueti.enums.StatusItem;
import br.com.cnec.estoqueti.enums.TipoControleItem;
import br.com.cnec.estoqueti.enums.TipoRegistroItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modelo_item_id", nullable = false)
    private ModeloItem modeloItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_controle", nullable = false, length = 20)
    private TipoControleItem tipoControle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_atual_id", nullable = false)
    private Local localAtual;

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<RegistroCondicao> registrosCondicao = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false, length = 30)
    private TipoRegistroItem tipoRegistro;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status_item", nullable = false, length = 30)
    private StatusItem statusItem = StatusItem.DISPONIVEL;

    @Column(length = 50)
    private String patrimonio;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Builder.Default
    @OneToMany(mappedBy = "itemPai")
    List<ItemComponente> vinculosDeComponetes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "itemComponente")
    List<ItemComponente> historicoComoComponente = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }

        if (ativo == null) {
            ativo = true;
        }

        if (quantidade == null) {
            quantidade = 1;
        }

        if (statusItem == null) {
            statusItem = StatusItem.DISPONIVEL;
        }

        if (tipoRegistro == null) {
            tipoRegistro = TipoRegistroItem.UNIDADE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}