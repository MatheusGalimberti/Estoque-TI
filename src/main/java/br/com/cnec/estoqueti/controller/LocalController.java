package br.com.cnec.estoqueti.controller;

import br.com.cnec.estoqueti.dto.local.LocalRequestDTO;
import br.com.cnec.estoqueti.dto.local.LocalResponseDTO;
import br.com.cnec.estoqueti.entity.Local;
import br.com.cnec.estoqueti.mapper.LocalMapper;
import br.com.cnec.estoqueti.service.LocalService;
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
@RequestMapping("/api/locais")
public class LocalController {

    private final LocalService localService;
    private final LocalMapper localMapper;

    public LocalController(
            LocalService localService,
            LocalMapper localMapper
    ) {
        this.localService = localService;
        this.localMapper = localMapper;
    }

    @PostMapping
    public ResponseEntity<LocalResponseDTO> cadastrar(
            @Valid @RequestBody LocalRequestDTO request
    ) {
        Local local = localService.cadastrar(
                localMapper.toLocal(request),
                request.tipo()
        );

        LocalResponseDTO responseDTO =
                localMapper.toResponse(local);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(local.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<LocalResponseDTO>> listarLocais(){
        List<LocalResponseDTO> locais = localService.listarLocaisAtivos()
                .stream()
                .map(localMapper::toResponse)
                .toList();

        return ResponseEntity.ok(locais);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalResponseDTO> buscarPorId(
            @PathVariable Long id){
        Local local = localService.buscarPorId(id);
        LocalResponseDTO response = localMapper.toResponse(local);

        return ResponseEntity.ok(response);

    }
}