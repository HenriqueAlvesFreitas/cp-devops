package br.com.fiap.snapsenhas.config;

import br.com.fiap.snapsenhas.models.Usuario;
import br.com.fiap.snapsenhas.repositories.UsuarioRepository;
import br.com.fiap.snapsenhas.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthorizationFilter extends OncePerRequestFilter{

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //pegar o token do header
        var token = getToken(request);
        // se for valido, autenticar o usuario
        if (token != null){
            String email = tokenService.valide(token);
            Usuario usuario = repository.findByEmail(email).get();
            Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // chama o próximo
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization"); // Bearer aksjhdfkjashdfkajsdhlkfjasdflkj

        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.replace("Bearer ", "");
    }
}
