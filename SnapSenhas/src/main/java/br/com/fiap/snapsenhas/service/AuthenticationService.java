package br.com.fiap.snapsenhas.service;

import br.com.fiap.snapsenhas.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService{

    @Autowired
    UsuarioRepository Repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuário " + username + " não encontrado!"));
    }
}
