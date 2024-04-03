package com.converter.pdf.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

public class DocxConverter implements FileConverter{

    @Override
    public void convert(Path inputFile, Path outputFile) throws IOException {
        try (InputStream is = Files.newInputStream(inputFile);
                OutputStream out = Files.newOutputStream(outputFile)) {
                ZipSecureFile.setMinInflateRatio(0.005);
                XWPFDocument document = new XWPFDocument(is);
                PdfOptions options = PdfOptions.create();
                PdfConverter.getInstance().convert(document, out, options);
        }
    }

}
