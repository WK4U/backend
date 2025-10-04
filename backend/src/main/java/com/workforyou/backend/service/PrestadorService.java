package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.repository.PrestadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrestadorService {

    @Autowired
    private PrestadorRepository prestadorRepository; // Você precisará deste repositório

    /**
     * Cria e salva a entidade Prestador no banco de dados.
     * @param request O objeto de requisição de registro.
     * @param pessoaJuridica A entidade PessoaJuridica já persistida.
     * @return A entidade Prestador salva.
     */
    public Prestador criarPrestador(RegistroRequest request, PessoaJuridica pessoaJuridica) {
        Prestador prestador = new Prestador();

        prestador.setEmail(request.getEmail());
        prestador.setUrlFoto(request.getUriFoto());
        prestador.setEspecialidade(request.getEspecialidade());

        prestador.setPessoaJuridica(pessoaJuridica);

        return prestadorRepository.save(prestador);
    }
}
