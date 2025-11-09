package com.web.milhas.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
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
public class RelatorioService {

    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] gerarCsvMovimentacoes(String emailUsuario) {
        UsuarioEntity usuario = buscarUsuarioPorEmail(emailUsuario);
        List<MovimentacaoPontosEntity> movimentacoes = movimentacaoRepository.findBySaldoPontosUsuarioId(usuario.getId());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos);
             CSVWriter writer = new CSVWriter(osw)) {

            writer.writeNext(new String[]{"Data", "Tipo", "Programa", "Pontos", "Descrição"});

            for (MovimentacaoPontosEntity mov : movimentacoes) {
                writer.writeNext(new String[]{
                        mov.getDataMovimentacao().format(DATE_FORMATTER),
                        mov.getTipo().name(),
                        mov.getSaldoPontos().getProgramaPontos().getNome(),
                        mov.getQuantidadePontos().toString(),
                        mov.getDescricao()
                });
            }

            osw.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }

    public byte[] gerarPdfMovimentacoes(String emailUsuario) {
        UsuarioEntity usuario = buscarUsuarioPorEmail(emailUsuario);
        List<MovimentacaoPontosEntity> movimentacoes = movimentacaoRepository.findBySaldoPontosUsuarioId(usuario.getId());

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

            for (MovimentacaoPontosEntity mov : movimentacoes) {
                table.addCell(mov.getDataMovimentacao().format(DATE_FORMATTER));
                table.addCell(mov.getTipo().name());
                table.addCell(mov.getSaldoPontos().getProgramaPontos().getNome());
                table.addCell(mov.getQuantidadePontos().toString());
                table.addCell(mov.getDescricao() != null ? mov.getDescricao() : "-");
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