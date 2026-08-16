package br.com.cnec.estoqueti.dto.categoria;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CategoriaResponseDTO(

        Long id,
        String nome,
        String descricao,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
