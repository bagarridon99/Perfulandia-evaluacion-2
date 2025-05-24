package com.example.perfulandia.autentificacion.service; // O tu paquete elegido

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.service.UsuarioService; //
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // Para las autoridades/roles, si no tienes roles definidos aún

@Service("userDetailsService") // Es importante darle un nombre al bean si tienes varias implementaciones
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Autowired
    public UserDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {


        UsuarioModel usuarioModel = usuarioService.buscarPorEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + usernameOrEmail));

        // Por ahora, no estamos manejando roles/autoridades. Si los tuvieras en UsuarioModel, los cargarías aquí.
        // El tercer argumento de User() es una colección de GrantedAuthority.
        return new User(usuarioModel.getEmail(), usuarioModel.getPassword(), new ArrayList<>());
    }
}