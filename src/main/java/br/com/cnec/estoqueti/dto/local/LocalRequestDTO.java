package br.com.cnec.estoqueti.dto.local;

import br.com.cnec.estoqueti.enums.TipoLocal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocalRequestDTO(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100, message = "O nome do local está muito grande")
        String nome,

        @NotNull(message = "O tipo de local é obrigatório.")
        TipoLocal tipo,

        @Size(max = 255, message = "A descricao deve posssuir no maximo 255 caracteres")
        String descricao
) {
}
