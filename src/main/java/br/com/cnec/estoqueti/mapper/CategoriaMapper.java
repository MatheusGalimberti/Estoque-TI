package br.com.cnec.estoqueti.mapper;

import br.com.cnec.estoqueti.dto.categoria.CategoriaRequestDTO;
import br.com.cnec.estoqueti.dto.categoria.CategoriaResponseDTO;
import br.com.cnec.estoqueti.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toCategoria(CategoriaRequestDTO request){

        return Categoria.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .build();
    }

    public CategoriaResponseDTO toResponse(Categoria categoria){
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativo(categoria.getAtivo())
                .criadoEm(categoria.getCriadoEm())
                .atualizadoEm(categoria.getAtualizadoEm())
                .build();
    }
}
