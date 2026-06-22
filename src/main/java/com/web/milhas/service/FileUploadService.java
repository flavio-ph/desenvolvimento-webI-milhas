package com.web.milhas.service;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    void armazenarComprovante(MultipartFile arquivo, Long compraId, String emailUsuario);

    Resource loadAsResource(String nomeArquivo);
}