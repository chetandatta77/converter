package com.converter.pdf.tool;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DuplicateConverter implements FileConverter {

    @Override
    public void convert(Path inputFile, Path outputFile) throws Exception {
        Files.copy(inputFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
    }

}
