package com.converter.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfApplication.class, args);
		try {
			Conversion.convertUsingParallelThreads();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
