package com.github.esslerc.pdfamaker.service;

import com.github.esslerc.pdfamaker.domain.PDFAStandard;

import java.util.Locale;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public record SettingsData(String outputPath, PDFAStandard pdfaStandard, Locale locale) {

    public SettingsData {
        Objects.requireNonNull(outputPath, "outputPath must not be null");
        if (outputPath.isBlank()) {
            throw new IllegalArgumentException("outputPath must not be blank");
        }

        Objects.requireNonNull(pdfaStandard, "pdfaStandard must not be null");
        Objects.requireNonNull(locale, "locale must not be null");

        Path path = Path.of(outputPath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("outputPath does not exist: " + outputPath);
        }
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("outputPath is not a directory: " + outputPath);
        }
        if (!Files.isWritable(path)) {
            throw new IllegalArgumentException("outputPath is not writable: " + outputPath);
        }
    }
}


