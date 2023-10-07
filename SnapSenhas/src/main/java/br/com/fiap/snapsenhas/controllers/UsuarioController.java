package br.com.fiap.snapsenhas.controllers;

import br.com.fiap.snapsenhas.models.Credencial;
import br.com.fiap.snapsenhas.models.GeradorDeSenhas;
import br.com.fiap.snapsenhas.models.Usuario;
import br.com.fiap.snapsenhas.repositories.UsuarioRepository;
import br.com.fiap.snapsenhas.service.TokenService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UsuarioController {
    @Autowired
    UsuarioRepository repo;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody @Valid Usuario usuario){
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        repo.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid Credencial credencial){
        manager.authenticate(credencial.toAuthentication());
        var token = tokenService.generateToken(credencial);
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Usuario> delete(@PathVariable Long id){
        Usuario user = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "usuario não encontrado"));
        repo.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody @Valid Usuario usuario){
        Usuario user = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "usuario não encontrado"));
                BeanUtils.copyProperties(usuario, user, "id");
        repo.save(user);
        return ResponseEntity.ok(user);
    }

   
    @GetMapping("/user")
    public List<Usuario> show(){

        return repo.findAll();
    }
   

    
}
