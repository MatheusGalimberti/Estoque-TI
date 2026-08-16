package br.com.cnec.estoqueti.mapper;

import br.com.cnec.estoqueti.dto.condicao.CondicaoRequestDTO;
import br.com.cnec.estoqueti.dto.condicao.CondicaoResponseDTO;
import br.com.cnec.estoqueti.entity.Condicao;
import org.springframework.stereotype.Component;

@Component
public class CondicaoMapper {

    public CondicaoResponseDTO toResponse(Condicao condicao){

        return CondicaoResponseDTO.builder()
                .id(condicao.getId())
                .nome(condicao.getNome())
                .descricao(condicao.getDescricao())
                .ativo(condicao.getAtivo())
                .criadoEm(condicao.getCriadoEm())
                .atualizadoEm(condicao.getAtualizadoEm())
                .build();
    }


    public Condicao toCondicao(CondicaoRequestDTO request){

        return Condicao.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .build();
    }


}
