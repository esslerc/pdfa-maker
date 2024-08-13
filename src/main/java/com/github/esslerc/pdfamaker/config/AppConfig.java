package com.github.esslerc.pdfamaker.config;

import com.github.esslerc.pdfamaker.service.PDFAStandard;

public class AppConfig {

    private String outputPath;
    private PDFAStandard pdfaStandard;

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public PDFAStandard getPdfaStandard() {
        return pdfaStandard;
    }

    public void setPdfaStandard(PDFAStandard pdfaStandard) {
        this.pdfaStandard = pdfaStandard;
    }
}
