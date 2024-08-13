package com.github.esslerc.pdfamaker.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.github.esslerc.pdfamaker.service.PDFAStandard;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

public class ConfigServiceTest {

    private ConfigService configService;
    private Path tempConfigDir;
    private Path tempConfigFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        tempConfigDir = tempDir.resolve(".pdfa-maker");
        Files.createDirectories(tempConfigDir);
        tempConfigFile = tempConfigDir.resolve("app.properties");
        Files.createFile(tempConfigFile);
        configService = spy(new ConfigService() {
            @Override
            protected String getConfigDirPath() {
                return tempConfigDir.toString();
            }
        });
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempConfigFile);
        Files.deleteIfExists(tempConfigDir);
    }

    @Test
    public void testLoadPropertiesIntoBean() throws IOException {
        try (OutputStream output = new FileOutputStream(tempConfigFile.toFile())) {
            Properties properties = new Properties();
            properties.setProperty("pdfaStandard", PDFAStandard.PDFA_2b.getLabel());
            properties.setProperty("outputPath", tempDir.toAbsolutePath().toString());
            properties.store(output, null);
        }

        configService.loadProperties();
        configService.loadPropertiesIntoBean();
        AppConfig appConfig = configService.getAppConfig();

        assertEquals(PDFAStandard.PDFA_2b, appConfig.getPdfaStandard());
        assertEquals(tempDir.toAbsolutePath().toString(), appConfig.getOutputPath());
    }

    @Test
    public void testSavePropertiesFromBean() {
        AppConfig appConfig = configService.getAppConfig();
        appConfig.setOutputPath(tempDir.toAbsolutePath().toString());
        appConfig.setPdfaStandard(PDFAStandard.PDFA_1b);
        configService.savePropertiesFromBean();

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(tempConfigFile.toFile())) {
            properties.load(input);
        } catch (IOException e) {
            fail("Exception should not be thrown");
        }

        assertEquals(tempDir.toAbsolutePath().toString(), properties.getProperty("outputPath"));
        assertEquals(PDFAStandard.PDFA_1b.getLabel(), properties.getProperty("pdfaStandard"));
    }

    @Test
    public void testLoadEmptyPropertiesIntoBean() throws IOException {
        try (OutputStream output = new FileOutputStream(tempConfigFile.toFile())) {
            Properties properties = new Properties();
            properties.store(output, null);
        }

        configService.loadProperties();
        configService.loadPropertiesIntoBean();
        AppConfig appConfig = configService.getAppConfig();

        assertNull(appConfig.getOutputPath());
        assertNull(appConfig.getPdfaStandard());
    }
}
