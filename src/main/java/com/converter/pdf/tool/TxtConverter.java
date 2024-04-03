package com.converter.pdf.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class TxtConverter implements FileConverter{

    @Override
    public void convert(Path inputFile, Path outputFile) throws IOException, DocumentException {
        try (InputStream is = Files.newInputStream(inputFile);
            OutputStream out = Files.newOutputStream(outputFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            String line;
            while ((line = br.readLine()) != null) {
                document.add(new Paragraph(line));
            }
            document.close();
        }
    }

}
