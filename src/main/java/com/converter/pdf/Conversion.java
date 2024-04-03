package com.converter.pdf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.converter.pdf.tool.FileConverter;
import com.converter.pdf.tool.FileConverterFactory;

public class Conversion {

    public static void convertUsingParallelThreads() throws IOException {
        Path inputDir = Paths.get("src\\main\\resources\\input\\");
        Path outputDir = Paths.get("src\\main\\resources\\output\\");
        int nThreads = 10;

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        try (Stream<Path> paths = Files.walk(inputDir)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        executor.submit(() -> {
                            try {
                                converterToPdfUsingFactory(path, outputDir);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt(); // Rethrow the InterruptedException by interrupting the current thread
        }
    }

    private static void converterToPdfUsingFactory(Path inputFile, Path outputDir) throws Exception {
        String fileName = inputFile.getFileName().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
        Path outputFile = outputDir.resolve(outputFileName);

        FileConverter converter = FileConverterFactory.getConverter(fileExtension);
        if (converter != null) {
            converter.convert(inputFile, outputFile);
        } else {
            System.out.println("Unsupported file type: " + fileExtension);
        }
    }

}
