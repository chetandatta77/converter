package com.converter.pdf.tool;

import java.util.HashMap;
import java.util.Map;

public class FileConverterFactory {
    private static final Map<String, FileConverter> converters = new HashMap<>();

    static {
        
        converters.put("xml", new XmlConverter());
        converters.put("html", new HtmlConverter());
        converters.put("txt", new TxtConverter());
        
        converters.put("docx", new DocxConverter());
        converters.put("doc", new DocxConverter());

        converters.put("png", new ImgConverter());
        converters.put("jpg", new ImgConverter());
        converters.put("jpeg", new ImgConverter());

        converters.put("pdf", new DuplicateConverter());
        converters.put("tif", new DuplicateConverter());
        converters.put("tiff", new DuplicateConverter());
    }
    public static FileConverter getConverter(String fileExtension) {
        String ext = fileExtension.toLowerCase();
        return converters.get(ext);
    }
}
