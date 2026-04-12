package br.com.fiap.easypark.configs;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
@ConditionalOnBean(FirebaseAuth.class)
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseProperties properties;

    public FirebaseAuthenticationFilter(FirebaseAuth firebaseAuth, FirebaseProperties properties) {
        this.firebaseAuth = firebaseAuth;
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = bearerToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(token);
            var principal = new FirebasePrincipal(
                    decoded.getUid(),
                    decoded.getEmail(),
                    decoded.getName(),
                    decoded.getClaims()
            );
            var authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    token,
                    authorities(decoded.getClaims())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (FirebaseAuthException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Firebase ID token invalido.");
        }
    }

    private String bearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private Collection<GrantedAuthority> authorities(Map<String, Object> claims) {
        Set<String> roles = new LinkedHashSet<>();
        addClaimRoles(roles, claims.get("roles"));
        addClaimRoles(roles, claims.get("role"));
        addClaimRoles(roles, claims.get("perfil"));
        if (roles.isEmpty()) {
            roles.add(properties.defaultRole());
        }

        var authorities = new ArrayList<GrantedAuthority>();
        roles.forEach(role -> mapRole(role).forEach(mapped -> authorities.add(new SimpleGrantedAuthority(mapped))));
        return authorities;
    }

    private void addClaimRoles(Set<String> roles, Object claim) {
        if (claim instanceof Collection<?> values) {
            values.forEach(value -> addClaimRoles(roles, value));
            return;
        }
        if (claim instanceof String text && !text.isBlank()) {
            for (String role : text.split(",")) {
                if (!role.isBlank()) {
                    roles.add(role.trim());
                }
            }
        }
    }

    private Set<String> mapRole(String role) {
        String normalized = role.toUpperCase(Locale.ROOT).replace("ROLE_", "");
        return switch (normalized) {
            case "ADMIN" -> Set.of("ROLE_MOTORISTA", "ROLE_OPERADOR", "ROLE_ADMIN");
            case "OPERADOR" -> Set.of("ROLE_MOTORISTA", "ROLE_OPERADOR");
            case "CLIENTE", "MOTORISTA" -> Set.of("ROLE_MOTORISTA");
            default -> Set.of("ROLE_" + normalized);
        };
    }
}
