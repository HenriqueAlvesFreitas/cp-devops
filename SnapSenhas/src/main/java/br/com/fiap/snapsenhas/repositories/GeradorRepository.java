package br.com.fiap.snapsenhas.repositories;

import br.com.fiap.snapsenhas.models.GeradorDeSenhas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GeradorRepository extends JpaRepository<GeradorDeSenhas, Long> {
    Page<GeradorDeSenhas> findBySenhaContainingIgnoreCase(String senha, Pageable pageable);
}
