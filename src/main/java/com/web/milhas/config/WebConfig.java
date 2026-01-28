// src/main/java/com/web/milhas/config/WebConfig.java
package com.web.milhas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        
        // Define que requisições para /uploads/ nome_da_foto buscarão o arquivo na pasta física
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path);
    }
}