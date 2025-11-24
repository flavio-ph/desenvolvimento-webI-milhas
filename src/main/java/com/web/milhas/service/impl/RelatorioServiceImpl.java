package com.web.milhas.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.MovimentacaoService;
import com.web.milhas.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioServiceImpl implements RelatorioService {

    private final MovimentacaoService movimentacaoService;
    private final UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public byte[] gerarCsvMovimentacoes(String emailUsuario) {
        List<MovimentacaoPontosResponse> movimentacoes = movimentacaoService.listarMovimentacoes(emailUsuario);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos);
             CSVWriter writer = new CSVWriter(osw)) {

            writer.writeNext(new String[]{"Data", "Tipo", "Programa", "Pontos", "Descrição"});

            for (MovimentacaoPontosResponse mov : movimentacoes) {
                writer.writeNext(new String[]{
                        mov.dataMovimentacao().format(DATE_FORMATTER),
                        mov.tipo().name(),
                        mov.nomePrograma(),
                        mov.quantidadePontos().toString(),
                        mov.descricao()
                });
            }

            osw.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }

    @Override
    public byte[] gerarPdfMovimentacoes(String emailUsuario) {
        UsuarioEntity usuario = buscarUsuarioPorEmail(emailUsuario);
        List<MovimentacaoPontosResponse> movimentacoes = movimentacaoService.listarMovimentacoes(emailUsuario);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph titulo = new Paragraph("Extrato de Movimentações", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("Usuário: " + usuario.getNome()));
            document.add(new Paragraph("Gerado em: " + java.time.LocalDateTime.now().format(DATE_FORMATTER)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5); 
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 3, 2, 4});


            adicionarCabecalhoTabela(table, "Data");
            adicionarCabecalhoTabela(table, "Tipo");
            adicionarCabecalhoTabela(table, "Programa"); 
            adicionarCabecalhoTabela(table, "Pontos");
            adicionarCabecalhoTabela(table, "Descrição");

            for (MovimentacaoPontosResponse mov : movimentacoes) {
                table.addCell(mov.dataMovimentacao().format(DATE_FORMATTER));
                table.addCell(mov.tipo().name());
                table.addCell(mov.nomePrograma()); 
                table.addCell(mov.quantidadePontos().toString());
                table.addCell(mov.descricao() != null ? mov.descricao() : "-");
            }

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private void adicionarCabecalhoTabela(PdfPTable table, String titulo) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        table.addCell(header);
    }

    private UsuarioEntity buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }
}