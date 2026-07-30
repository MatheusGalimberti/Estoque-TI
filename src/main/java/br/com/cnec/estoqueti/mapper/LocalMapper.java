package br.com.cnec.estoqueti.mapper;

import br.com.cnec.estoqueti.dto.local.LocalRequestDTO;
import br.com.cnec.estoqueti.dto.local.LocalResponseDTO;
import br.com.cnec.estoqueti.entity.Local;
import org.springframework.stereotype.Component;

@Component
public class LocalMapper {

    public Local toLocal(LocalRequestDTO request) {

        return Local.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .build();
    }

    public LocalResponseDTO toResponse(Local local) {
        return LocalResponseDTO.builder()
                .id(local.getId())
                .nome(local.getNome())
                .tipo(local.getTipo())
                .descricao(local.getDescricao())
                .ativo(local.getAtivo())
                .criadoEm(local.getCriadoEm())
                .atualizadoEm(local.getAtualizadoEm())
                .build();
    }

}
