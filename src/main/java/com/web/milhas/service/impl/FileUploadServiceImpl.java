package com.web.milhas.service.impl;

import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.ComprovanteCompraEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.ComprovanteCompraRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final CompraRepository compraRepository;
    private final ComprovanteCompraRepository comprovanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final Path storageLocation;

    public FileUploadServiceImpl(
            CompraRepository compraRepository,
            ComprovanteCompraRepository comprovanteRepository,
            UsuarioRepository usuarioRepository,
            @Value("${file.upload-dir}") String uploadDir) {

        this.compraRepository = compraRepository;
        this.comprovanteRepository = comprovanteRepository;
        this.usuarioRepository = usuarioRepository;
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.storageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório para upload em: " + this.storageLocation,
                    ex);
        }
    }

    private static final Set<String> EXTENSOES_COMPROVANTE_PERMITIDAS = Set.of(".pdf", ".jpg", ".jpeg", ".png");

    @Override
    @Transactional
    public void armazenarComprovante(MultipartFile arquivo, Long compraId, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        CompraEntity compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada."));

        if (!compra.getCartao().getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Compra não pertence ao usuário.");
        }

        String nomeOriginal = StringUtils.cleanPath(arquivo.getOriginalFilename());
        if (nomeOriginal.contains("..")) {
            throw new RuntimeException("Nome de arquivo inválido.");
        }

        String extensao = "";
        int i = nomeOriginal.lastIndexOf('.');
        if (i > 0) {
            extensao = nomeOriginal.substring(i).toLowerCase();
        }

        if (!EXTENSOES_COMPROVANTE_PERMITIDAS.contains(extensao)) {
            throw new IllegalArgumentException(
                    "Tipo de arquivo não permitido. Use: pdf, jpg, jpeg ou png.");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("Content-Type inválido. Envie uma imagem ou PDF.");
        }

        String nomeUnico = UUID.randomUUID().toString() + extensao;

        try {
            Path targetLocation = this.storageLocation.resolve(nomeUnico);
            Files.copy(arquivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            ComprovanteCompraEntity comprovante = new ComprovanteCompraEntity();
            comprovante.setCompra(compra);
            comprovante.setNomeArquivo(nomeOriginal);
            comprovante.setTipoArquivo(arquivo.getContentType());
            comprovante.setUrlArquivo(targetLocation.toString());

            comprovanteRepository.save(comprovante);

        } catch (IOException ex) {
            throw new RuntimeException("Erro ao salvar o comprovante.", ex);
        }
    }
}