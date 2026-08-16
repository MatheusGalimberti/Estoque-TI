package br.com.cnec.estoqueti.controller;

import br.com.cnec.estoqueti.dto.categoria.CategoriaRequestDTO;
import br.com.cnec.estoqueti.dto.categoria.CategoriaResponseDTO;
import br.com.cnec.estoqueti.entity.Categoria;
import br.com.cnec.estoqueti.mapper.CategoriaMapper;
import br.com.cnec.estoqueti.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;
    private final CategoriaMapper mapper;


    public CategoriaController(CategoriaService service,
                               CategoriaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias(){

        List<CategoriaResponseDTO> categorias =
                service.listarAtivas()
                        .stream()
                        .map(mapper::toResponse)
                        .toList();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(
            @PathVariable Long id){

        CategoriaResponseDTO categoria = mapper.toResponse(
          service.buscarPorId(id)
        );

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> cadastrar(
           @Valid @RequestBody CategoriaRequestDTO request){

        Categoria categoria = service.cadastrar(
                mapper.toCategoria(request)
        );

        CategoriaResponseDTO response = mapper.toResponse(categoria);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoria.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);

    }

}
