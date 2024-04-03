package com.converter.pdf.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class HtmlConverter implements FileConverter{

    @Override
    public void convert(Path inputFile, Path outputFile) throws IOException {
        try (InputStream is = Files.newInputStream(inputFile);
            OutputStream os = Files.newOutputStream(outputFile)) {
    
            // Read HTML into a String
            String html = new String(Files.readAllBytes(inputFile), StandardCharsets.UTF_8);
    
            // Parse HTML with Jsoup and output as pretty-printed XHTML
            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(html, "", Parser.xmlParser());
            jsoupDoc.select("script").remove();
                // Convert relative image URLs to absolute URLs
            String baseUri = inputFile.toUri().toString();
            for (Element img : jsoupDoc.select("img[src]")) {
                String src = img.attr("src");
                if (!src.startsWith("http")) {
                    img.attr("src", baseUri + src);
                }
            }
            html = jsoupDoc.outerHtml();
    
            // Convert HTML to PDF
            new PdfRendererBuilder()
                .useFastMode()
                .withHtmlContent(html, null)
                .toStream(os)
                .run();
        }       
    }

}
