package br.com.cnec.estoqueti.dto.condicao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CondicaoRequestDTO (

        @NotBlank
        @Size(max = 100, message = "nome no maximo com 100 caracteres")
        String nome,

        @Size(max = 255,message = "descrição com no maximo 255 caracteres")
        String descricao
){
}
