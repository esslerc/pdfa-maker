package com.github.esslerc.pdfamaker.service;

import com.github.esslerc.pdfamaker.domain.PDFAStandard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsServiceTest {

    private SettingsService settingsService;
    private Path propsFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        propsFile = tempDir.resolve(".pdfa-maker/app.properties");
        Files.createDirectories(propsFile.getParent());
        settingsService = new SettingsService(propsFile.getParent());
    }

    @Test
    public void testLoadPropertiesIntoBean() throws IOException {
        try (OutputStream output = Files.newOutputStream(propsFile)) {
            Properties props = new Properties();
            props.setProperty("pdfaStandard", PDFAStandard.PDFA_2b.getLabel());
            props.setProperty("outputPath", tempDir.toAbsolutePath().toString());
            props.store(output, null);
        }

        settingsService.reloadProperties();
        SettingsData settingsData = settingsService.getSettingsData();

        assertEquals(PDFAStandard.PDFA_2b, settingsData.pdfaStandard());
        assertEquals(tempDir.toAbsolutePath().toString(), settingsData.outputPath());
    }

    @Test
    public void testSavePropertiesFromBean() {

        String newOutputPath = "/tmp/";
        SettingsData settingsData = new SettingsData(newOutputPath, PDFAStandard.PDFA_4, Locale.ENGLISH);
        settingsService.saveSettingsData(settingsData);

        SettingsData savedSettingsData = settingsService.getSettingsData();
        assertEquals(newOutputPath, savedSettingsData.outputPath());
        assertEquals(PDFAStandard.PDFA_4,savedSettingsData.pdfaStandard());
        assertEquals(Locale.ENGLISH, savedSettingsData.locale());
    }
}
