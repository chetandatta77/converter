package com.converter.pdf.tool;

import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ImgConverter implements FileConverter {

    @Override
    public void convert(Path inputFile, Path outputFile) throws Exception {
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
    }

}
