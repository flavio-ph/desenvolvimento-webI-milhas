package com.web.milhas.repository;

import com.web.milhas.entity.ComprovanteCompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ComprovanteCompraRepository extends JpaRepository<ComprovanteCompraEntity, Long> {
    Optional<ComprovanteCompraEntity> findByCompraId(Long compraId);

    @Query("SELECT c.compra.usuario.email FROM ComprovanteCompraEntity c WHERE c.caminhoArquivo LIKE %:nomeArquivo%")
    Optional<String> findDonoEmailByNomeArquivo(@Param("nomeArquivo") String nomeArquivo);




}