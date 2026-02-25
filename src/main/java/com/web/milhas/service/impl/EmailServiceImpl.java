package com.web.milhas.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

    private final JavaMailSender mailSender;
    private final String emailFrom;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:nao-responda@sistemamilhas.com}") String emailFrom) {
        this.mailSender = mailSender;
        this.emailFrom = emailFrom;
    }

    public void enviarCodigo2FA(String destinatarioEmail, String codigo) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(emailFrom);
            mensagem.setTo(destinatarioEmail);
            mensagem.setSubject("Código de Autenticação - 2FA");
            mensagem.setText("Seu código de verificação é: " + codigo + "\n\nEste código expira em 5 minutos.");

            mailSender.send(mensagem);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar email de autenticação: " + e.getMessage(), e);
        }
    }
}