package com.converter.pdf.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

public class XmlConverter implements FileConverter {
    @Override
    public void convert(Path inputFile, Path outputFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Step 2: Create XSL-FO
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File("stylesheet.xsl")));
        StreamSource source = new StreamSource(inputFile.toFile());
        StreamResult result = new StreamResult(new File("intermediate.fo"));
        transformer.transform(source, result);

        // Step 3: Generate PDF
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
       // Create a new Fop instance for PDF
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, new FileOutputStream(outputFile.toFile()));

        // Create a new Transformer instance for the XSLT transformation
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer foTransformer = tFactory.newTransformer();

        // Create a new Source instance for the XSL-FO file
        StreamSource foSource = new StreamSource(new File("intermediate.fo"));

        // Create a new Result instance for the Fop
        Result foResult = new SAXResult(fop.getDefaultHandler());

        // Transform the XSL-FO to PDF
        foTransformer.transform(foSource, foResult);

        System.out.println("PDF generated successfully.");

    }

}
