package com.converter.pdf.tool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;


public class HtmlConverter implements FileConverter{

    @Override
    public void convert(Path inputFile, Path outputFile) throws IOException {
        // usePDFRenderToConvert(inputFile, outputFile);   
        usingRenderText(inputFile, outputFile);    
    }

     @SuppressWarnings("null")
    public static void usingRenderText(Path inputFile, Path outputFile) {
            try {
                // Parse HTML input using Jsoup
                org.jsoup.nodes.Document htmlDocument = Jsoup.parse(inputFile.toFile(), "UTF-8");
    
                // Extract all img tags
                Elements imgElements = htmlDocument.select("img");
    
                // Process each img tag
                for (Element imgElement : imgElements) {
                    // Get the image URL
                    String imageUrl = imgElement.attr("src");
                    
                    // Download the image
                    byte[] imageBytes = downloadImage(imageUrl);
                    
                    // Embed the image into the HTML content
                    String imageDataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
                    imgElement.attr("src", imageDataUri);
                }
    
                // Use W3CDom to convert Jsoup document to W3C DOM
                W3CDom w3cDom = new W3CDom();
                Document document = w3cDom.fromJsoup(htmlDocument);
    
                // Create renderer instance
                ITextRenderer renderer = new ITextRenderer();
    
                // Set base URL for resolving relative URLs
                renderer.setDocument(document, inputFile.toUri().toString());
    
                // Render the PDF
                renderer.layout();
    
                // Create output stream
                try (OutputStream outputStream = new FileOutputStream(outputFile.toFile())) {
                    // Write PDF to the output stream
                    renderer.createPDF(outputStream);
                }
    
                System.out.println("PDF created successfully!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        private static byte[] downloadImage(String imageUrl) throws IOException {
            // Set the default proxy to NO_PROXY
            System.setProperty("java.net.useSystemProxies", "false");
            // Proxy defaultProxy = Proxy.NO_PROXY;
    
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                // If the URL does not have a protocol specified, prepend "http://" as a default
                imageUrl = "https://" + imageUrl;
            }
            
            System.out.println(imageUrl);
            // Open connection to the URL with NO_PROXY
            HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection(defaultProxy);
            connection.setRequestMethod("GET");
    
            // Retrieve the response
            return IOUtils.toByteArray(connection.getInputStream());
        }

    // private void usePDFRenderToConvert(Path inputFile, Path outputFile) throws IOException {
    //     try (InputStream is = Files.newInputStream(inputFile);
    //         OutputStream os = Files.newOutputStream(outputFile)) {
    
    //         // Read HTML into a String
    //         String html = new String(Files.readAllBytes(inputFile), StandardCharsets.UTF_8);
    
    //         // Parse HTML with Jsoup and output as pretty-printed XHTML
    //         org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(html, "", Parser.xmlParser());
    //         jsoupDoc.select("script").remove();
    //             // Convert relative image URLs to absolute URLs
    //         String baseUri = inputFile.toUri().toString();
    //         for (Element img : jsoupDoc.select("img[src]")) {
    //             String src = img.attr("src");
    //             if (!src.startsWith("http")) {
    //                 img.attr("src", baseUri + src);
    //             }
    //         }
    //         html = jsoupDoc.outerHtml();
    
    //         // Convert HTML to PDF
    //         new PdfRendererBuilder()
    //             .useFastMode()
    //             .withHtmlContent(html, null)
    //             .toStream(os)
    //             .run();
    //     }
    // }

}
