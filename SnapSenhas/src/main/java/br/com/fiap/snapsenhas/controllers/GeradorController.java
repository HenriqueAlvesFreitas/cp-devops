package br.com.fiap.snapsenhas.controllers;

import br.com.fiap.snapsenhas.models.GeradorDeSenhas;
import br.com.fiap.snapsenhas.repositories.GeradorRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("snapsenhas")
public class GeradorController {
    Logger log = LoggerFactory.getLogger(GeradorController.class);
    @Autowired
    GeradorRepository repo;

    @Autowired
    PagedResourcesAssembler<GeradorDeSenhas> assembler;
    private static final String Letras_Maiusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String Letras_Minusculas = "abcdefghijklmnopqrstuvwxyz";
    private static final String Numeros = "0123456789";
    private static final String Caracteres_Especiais = "!@#$%^&*()_+-=[]|,./?><";
    private static final String Todos_Caracteres =
            Letras_Maiusculas + Letras_Minusculas + Numeros + Caracteres_Especiais;

    @GetMapping
    public PagedModel<EntityModel<GeradorDeSenhas>> index(@PageableDefault(size = 5) Pageable pageable) {
        return assembler.toModel(repo.findAll(pageable));
    }

    @GetMapping("/pesquisa/{id}")
    public EntityModel<GeradorDeSenhas> show(@PathVariable Long id) {
        log.info("buscar senha com id: " + id);
        GeradorDeSenhas senha = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Senha não encontrada"));
        return senha.toModel();
    }

    @GetMapping("/gerador")
    @Operation(
            summary = "Gerar uma Senha"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Senha Gerada com Sucesso com sucesso"),
            @ApiResponse(responseCode = "400", description = "Algo deu errado, a senha não foi gerada")
    })
    public ResponseEntity<GeradorDeSenhas> create(@Valid GeradorDeSenhas geradorDeSenhas) {
        List<String> letras = Arrays.asList(Todos_Caracteres.split(""));
        Collections.shuffle(letras);
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int randomIndex = random.nextInt(letras.size());
            senha.append(letras.get(randomIndex));
        }
        log.info("cadastrando senha" + geradorDeSenhas);
        geradorDeSenhas.setSenha(senha.toString());
        repo.save(geradorDeSenhas);
        return ResponseEntity.status(HttpStatus.CREATED).body(geradorDeSenhas);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<GeradorDeSenhas> update(@PathVariable Long id, @RequestBody  @Valid GeradorDeSenhas senhaAtualizado) {
        log.info("atualizar o senha com id: " + id);
        GeradorDeSenhas senha = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Senha não encontrada"));
        BeanUtils.copyProperties(senhaAtualizado, senha, "id");
        repo.save(senha);
        return ResponseEntity.ok(senha);
    }
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<GeradorDeSenhas> destroy(@PathVariable Long id) {
        log.info("deletar senha com o id: " + id);

        GeradorDeSenhas senha = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Senha não encontrada"));
        repo.delete(senha);

        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationExceptions(ConstraintViolationException ex) {
        log.error("Erro de validação: ", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        log.error("Erro não esperado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }
}
