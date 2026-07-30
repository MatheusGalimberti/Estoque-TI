package br.com.cnec.estoqueti.dto.local;

import br.com.cnec.estoqueti.enums.TipoLocal;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LocalResponseDTO(

        Long id,
        String nome,
        TipoLocal tipo,
        String descricao,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm

) {
}
