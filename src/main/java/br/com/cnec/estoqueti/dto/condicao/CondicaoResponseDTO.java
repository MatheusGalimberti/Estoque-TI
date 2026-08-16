package br.com.cnec.estoqueti.dto.condicao;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CondicaoResponseDTO (

        Long id,
        String nome,
        String descricao,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm

){
}
