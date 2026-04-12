package br.com.fiap.easypark.services;

import br.com.fiap.easypark.entities.Usuario;
import br.com.fiap.easypark.repositories.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class EasyparkUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public EasyparkUserDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .authorities(authoritiesFor(usuario.getPerfil()))
                .accountLocked(usuario.isBloqueado())
                .build();
    }

    private List<SimpleGrantedAuthority> authoritiesFor(String perfil) {
        String normalized = perfil == null ? "cliente" : perfil.toLowerCase(Locale.ROOT);
        var authorities = new ArrayList<SimpleGrantedAuthority>();

        switch (normalized) {
            case "admin" -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_MOTORISTA"));
                authorities.add(new SimpleGrantedAuthority("ROLE_OPERADOR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            case "operador" -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_MOTORISTA"));
                authorities.add(new SimpleGrantedAuthority("ROLE_OPERADOR"));
            }
            default -> authorities.add(new SimpleGrantedAuthority("ROLE_MOTORISTA"));
        }

        return authorities;
    }
}
