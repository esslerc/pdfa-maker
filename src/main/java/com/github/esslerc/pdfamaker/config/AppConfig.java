package com.github.esslerc.pdfamaker.config;

import com.github.esslerc.pdfamaker.service.PDFAStandard;

import java.util.Locale;

public class AppConfig {

    private String outputPath;
    private PDFAStandard pdfaStandard;
    private Locale locale;

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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
