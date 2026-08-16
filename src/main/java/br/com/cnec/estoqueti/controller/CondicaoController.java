package br.com.cnec.estoqueti.controller;

import br.com.cnec.estoqueti.dto.condicao.CondicaoRequestDTO;
import br.com.cnec.estoqueti.dto.condicao.CondicaoResponseDTO;
import br.com.cnec.estoqueti.entity.Condicao;
import br.com.cnec.estoqueti.mapper.CondicaoMapper;
import br.com.cnec.estoqueti.service.CondicaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/condicoes")
public class CondicaoController {

    private final CondicaoService service;
    private final CondicaoMapper mapper;

    public CondicaoController(CondicaoService service,
                              CondicaoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    @PostMapping
    public ResponseEntity<CondicaoResponseDTO> cadastrar (
            @Valid @RequestBody CondicaoRequestDTO request) {

        Condicao condicao = service.cadastrar(
                mapper.toCondicao(request)
        );

        CondicaoResponseDTO response = mapper.toResponse(condicao);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(condicao.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);

    }

    @GetMapping("/todas")
    public ResponseEntity<List<CondicaoResponseDTO>> listarTodas(){

        List<CondicaoResponseDTO> condicoes = service
                .listarTodas()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(condicoes);
    }

    @GetMapping("/desativadas")
    public ResponseEntity<List<CondicaoResponseDTO>> listarDesativadas(){

        List<CondicaoResponseDTO> condicoes = service
                .listarDesativadas()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(condicoes);
    }

    @GetMapping()
    public ResponseEntity<List<CondicaoResponseDTO>> listarAtivas(){

        List<CondicaoResponseDTO> condicoes = service
                .listarAtivas()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(condicoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CondicaoResponseDTO> buscarPorId(
            @PathVariable Long id){

        CondicaoResponseDTO response = mapper.toResponse(
                    service.buscarPorId(id)
        );


        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<CondicaoResponseDTO> ativarCondicao(
            @PathVariable Long id){

        CondicaoResponseDTO response = mapper.toResponse(
                    service.ativarCondicao(id)
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<CondicaoResponseDTO> desativarCondicao(
            @PathVariable Long id){

        CondicaoResponseDTO response = mapper.toResponse(
                service.desativarCondicao(id)
        );

        return ResponseEntity.ok(response);
    }

}
