package com.web.milhas.service;

public interface RelatorioService {

    byte[] gerarCsvMovimentacoes(String emailUsuario);
    byte[] gerarPdfMovimentacoes(String emailUsuario);
}