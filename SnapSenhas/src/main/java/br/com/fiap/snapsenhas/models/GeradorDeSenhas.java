package br.com.fiap.snapsenhas.models;

import br.com.fiap.snapsenhas.controllers.GeradorController;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Entity
@Table(name = "tb_gerador")
@Getter
@Setter
public class GeradorDeSenhas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_senha")
    private Long id;

    @Column(name = "gd_senha")
    private String senha;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO",
            foreignKey = @ForeignKey(name = "FK_USUARIO_SENHA", value = ConstraintMode.CONSTRAINT))
    private Usuario usuario;

    public EntityModel<GeradorDeSenhas> toModel() {
        return EntityModel.of(
                this,
                linkTo(methodOn(GeradorController.class).show(id)).withSelfRel(),
                linkTo(methodOn(GeradorController.class).destroy(id)).withRel("delete"),
                linkTo(methodOn(GeradorController.class).index(Pageable.unpaged())).withRel("all")
        );
    }
}
