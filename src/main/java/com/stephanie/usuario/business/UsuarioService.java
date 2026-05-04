package com.stephanie.usuario.business;

import com.stephanie.usuario.business.converter.UsuarioConverter;
import com.stephanie.usuario.business.dto.UsuarioDTO;
import com.stephanie.usuario.infrastructure.entity.Usuario;
import com.stephanie.usuario.infrastructure.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
       Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
       return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }


}


