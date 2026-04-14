package br.com.fiap.easypark.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record EasyparkUserDetails(
        String email,
        String password,
        String displayName,
        boolean accountNonLocked,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public EasyparkUserDetails {
        displayName = hasText(displayName) ? displayName.trim() : email;
        authorities = List.copyOf(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
