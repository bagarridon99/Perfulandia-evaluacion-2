// Ubicación: src/main/java/com/example/perfulandia/autentificacion/service/UserDetailsServiceImpl.java
package com.example.perfulandia.autentificacion.service;

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Autowired
    public UserDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        // 1. Busca el usuario usando tu servicio (esto ya lo tenías bien)
        UsuarioModel usuarioModel = usuarioService.buscarPorEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + usernameOrEmail));

        // 2. Devuelve el objeto usuarioModel DIRECTAMENTE.
        // Como UsuarioModel implementa UserDetails, Spring Security ya sabe qué hacer con él.
        // Llamará automáticamente al método getAuthorities() que definimos antes.
        return usuarioModel;
    }
}