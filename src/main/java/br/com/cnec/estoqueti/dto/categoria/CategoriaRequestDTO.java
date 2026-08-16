package br.com.cnec.estoqueti.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(

        @NotBlank
        @Size(max = 100, message = "o nome deve ter no maximo 100 caracteres")
        String nome,

        @Size(max = 255, message = "a descrição deve ter no maximo 255 caracateres")
        String descricao
) {
}
