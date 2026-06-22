package com.web.milhas.controller;

import com.web.milhas.entity.ComprovanteCompraEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.ForbiddenException;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.ComprovanteCompraRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class ArquivoController {

    private final FileUploadService fileUploadService;
    private final ComprovanteCompraRepository comprovanteRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/{nomeArquivo}")
    public ResponseEntity<Resource> downloadArquivo(@PathVariable String nomeArquivo, Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();

        // Caso 1: É a foto de perfil do próprio usuário
        if (usuarioRepository.existsByEmailAndProfilePhoto(emailUsuarioLogado, nomeArquivo)) {
            return carregarArquivo(nomeArquivo);
        }

        // Caso 2: É um comprovante de compra (Busca o e-mail do dono via query otimizada)
        String emailDonoDoArquivo = comprovanteRepository.findDonoEmailByNomeArquivo(nomeArquivo)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado ou acesso negado."));

        // Validação de posse
        if (!emailDonoDoArquivo.equalsIgnoreCase(emailUsuarioLogado)) {
            throw new ForbiddenException("Você não tem permissão para acessar este documento.");
        }

        return carregarArquivo(nomeArquivo);
    }
}