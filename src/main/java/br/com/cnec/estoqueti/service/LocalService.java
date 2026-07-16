package br.com.cnec.estoqueti.service;

import br.com.cnec.estoqueti.entity.Local;
import br.com.cnec.estoqueti.enums.TipoLocal;
import br.com.cnec.estoqueti.exception.RegraNegocioException;
import br.com.cnec.estoqueti.repository.LocalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocalService {


    private final LocalRepository localRepository;

    public LocalService (LocalRepository localRepository){
        this.localRepository = localRepository;
    }

    @Transactional
    public Local cadastrar(Local local, TipoLocal tipoLocal){
        if(local == null){
            throw new RegraNegocioException("Local invalido");
        }

        if(tipoLocal == null){
            throw new RegraNegocioException("Tipo de local deve estar presente");
        }

        String nomeNormalizado = normalizarNome(local.getNome());
        validarExistencia(nomeNormalizado);

        local.setId(null);
        local.setTipo(tipoLocal);
        local.setNome(nomeNormalizado);
        local.setAtivo(true);

        return localRepository.save(local);
    }

    public List<Local> listarLocaisAtivos(){

        return localRepository.findAllByAtivoTrue();
    }

    public Local buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("O ID do local é obrigatório.");
        }

        return localRepository.findById(id)
                .orElseThrow(() ->
                        new RegraNegocioException("Local não encontrado.")
                );
    }


    private String normalizarNome(String nome){

        if(nome == null || nome.isBlank()){
            throw new RegraNegocioException("Nome do local invalido");
        }

        return nome.trim();
    }


    private void validarExistencia(String nome) {
        if (localRepository
                .existsByNomeIgnoreCaseAndAtivoTrue(nome)) {
            throw new RegraNegocioException(
                    "Já existe um local ativo com esse nome."
            );
        }

        if (localRepository
                .existsByNomeIgnoreCaseAndAtivoFalse(nome)) {
            throw new RegraNegocioException(
                    "Esse local já existe, mas está desativado."
            );
        }
    }


}
