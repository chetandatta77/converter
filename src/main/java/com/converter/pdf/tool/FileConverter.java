package com.converter.pdf.tool;

import java.nio.file.Path;

public interface FileConverter {
    void convert(Path inputFile, Path outputFile) throws Exception;
}