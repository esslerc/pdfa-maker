package com.github.esslerc.pdfamaker.converters;

public interface PDFConverter {
    void convertToPDFA(String inputPath, String outputPath, PDFAStandard pdfaStandard) throws Exception;
}
