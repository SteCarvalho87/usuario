package com.stephanie.usuario.business;

import com.stephanie.usuario.business.converter.UsuarioConverter;
import com.stephanie.usuario.business.dto.UsuarioDTO;
import com.stephanie.usuario.infrastructure.entity.Usuario;
import com.stephanie.usuario.infrastructure.exceptions.ConflictException;
import com.stephanie.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.stephanie.usuario.infrastructure.repository.UsuarioRepository;
import com.stephanie.usuario.infrastructure.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflictException("E-mail já cadastrado: " + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("E-mail já cadastrado", e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);

    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email não encontrado" + email));
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizarDadosUsuario(String token, UsuarioDTO dto){
       //remove obrigatoriedade de passar o email novamente
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //Criptografia da senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        //busca dados do usuario
       Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email não localizado"));

       //mescla dados da requisição com os dados do banco
       Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

       //Salva dados do usuario convertidos e converte resultado para usuarioDTO
       return usuarioConverter.paraUsuarioDTO((usuarioRepository.save(usuario)));
    }
}