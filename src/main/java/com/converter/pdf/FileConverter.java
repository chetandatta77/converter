package com.converter.pdf;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

public class FileConverter {

    public static void converter() throws Exception {
        Path inputDir = Paths.get("src\\main\\resources\\input\\");
        Path outputDir = Paths.get("src\\main\\resources\\output\\");

        try (Stream<Path> paths = Files.walk(inputDir)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            convertToPdf(path, outputDir);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }


    private static void convertToPdf(Path inputFile, Path outputDir) throws Exception {
        String fileName = inputFile.getFileName().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
        Path outputFile = outputDir.resolve(outputFileName);

        switch (fileExtension.toLowerCase()) {
            case "doc":
            case "docx":
                try (InputStream is = Files.newInputStream(inputFile);
                     OutputStream out = Files.newOutputStream(outputFile)) {
                        ZipSecureFile.setMinInflateRatio(0.005);
                        XWPFDocument document = new XWPFDocument(is);
                        PdfOptions options = PdfOptions.create();
                        PdfConverter.getInstance().convert(document, out, options);
                }
                break;
            case "html":
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
                break;
            case "txt":
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
                break;
            case "png":

            case "jpg":
            case "jpeg":
                try (PDDocument doc = new PDDocument()) {
                    PDPage page = new PDPage();
                    doc.addPage(page);

                    PDImageXObject pdImage = PDImageXObject.createFromFile(inputFile.toString(), doc);
                    PDPageContentStream contentStream = new PDPageContentStream(doc, page);

                    // Get page and image dimensions
                    PDRectangle pageSize = page.getMediaBox();
                    float pageWidth = pageSize.getWidth();
                    float pageHeight = pageSize.getHeight();
                    float imageWidth = pdImage.getWidth();
                    float imageHeight = pdImage.getHeight();

                    // Calculate scale factors
                    float scaleX = pageWidth / imageWidth;
                    float scaleY = pageHeight / imageHeight;

                    // Use the smaller scale factor to ensure the image fits on the page
                    float scale = Math.min(scaleX, scaleY);

                    // Calculate new image dimensions
                    float newImageWidth = imageWidth * scale;
                    float newImageHeight = imageHeight * scale;

                    // Draw image at center of page
                    float x = (pageWidth - newImageWidth) / 2;
                    float y = (pageHeight - newImageHeight) / 2;
                    contentStream.drawImage(pdImage, x, y, newImageWidth, newImageHeight);

                    contentStream.close();

                    doc.save(outputFile.toString());
                }
                break;
            case "pdf":
            case "tif":
            case "tiff":
                Files.copy(inputFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
                
                break;
            default:
                System.out.println("Unsupported file type: " + fileExtension);
        }
    }
}
