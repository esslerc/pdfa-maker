package com.github.esslerc.pdfamaker.service;

import com.github.esslerc.pdfamaker.domain.PDFAStandard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsDataTest {

    @TempDir
    Path tempDir;

    @Test
    public void testValidSettingsDataCreation() {
        SettingsData data = new SettingsData(
                tempDir.toString(),
                PDFAStandard.PDFA_2b,
                Locale.ENGLISH);

        assertEquals(tempDir.toString(), data.outputPath());
        assertEquals(PDFAStandard.PDFA_2b, data.pdfaStandard());
        assertEquals(Locale.ENGLISH, data.locale());
    }

    @Test
    public void testNullOutputPathThrowsException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                new SettingsData(null, PDFAStandard.PDFA_2b, Locale.ENGLISH));

        assertEquals("outputPath must not be null", ex.getMessage());
    }

    @Test
    public void testBlankOutputPathThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new SettingsData("  ", PDFAStandard.PDFA_2b, Locale.ENGLISH));

        assertEquals("outputPath must not be blank", ex.getMessage());
    }

    @Test
    public void testNullPdfaStandardThrowsException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                new SettingsData(tempDir.toString(), null, Locale.ENGLISH));

        assertEquals("pdfaStandard must not be null", ex.getMessage());
    }

    @Test
    public void testNullLocaleThrowsException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                new SettingsData(tempDir.toString(), PDFAStandard.PDFA_2b, null));

        assertEquals("locale must not be null", ex.getMessage());
    }

    @Test
    public void testNonExistingOutputPathThrowsException() {
        String nonExistingPath = tempDir.resolve("nonexistent").toString();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new SettingsData(nonExistingPath, PDFAStandard.PDFA_2b, Locale.ENGLISH));

        assertTrue(ex.getMessage().startsWith("outputPath does not exist"));
    }

    @Test
    public void testOutputPathIsNotDirectoryThrowsException() throws Exception {
        Path file = tempDir.resolve("somefile.txt");
        Files.createFile(file);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new SettingsData(file.toString(), PDFAStandard.PDFA_2b, Locale.ENGLISH));
        assertTrue(ex.getMessage().startsWith("outputPath is not a directory"));
    }

    @Test
    public void testOutputPathNotWritableThrowsException() throws Exception {
        Path dir = tempDir.resolve("readonlyDir");
        Files.createDirectory(dir);
        dir.toFile().setWritable(false);

        try {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    new SettingsData(dir.toString(), PDFAStandard.PDFA_2b, Locale.ENGLISH));

            assertTrue(ex.getMessage().startsWith("outputPath is not writable"));
        } finally {
            dir.toFile().setWritable(true);
        }
    }
}
