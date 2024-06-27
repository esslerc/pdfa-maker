package com.github.esslerc.pdfamaker.converters;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class PDFBoxConverterTest {

    private MockedStatic<PDDocument> pdDocumentStaticMock;
    private PDDocument pdDocumentMock;
    private File inputFileMock;
    private ByteArrayOutputStream baos;

    @BeforeEach
    public void setUp() {
        pdDocumentStaticMock = Mockito.mockStatic(PDDocument.class);
        pdDocumentMock = Mockito.mock(PDDocument.class);
        inputFileMock = Mockito.mock(File.class);
        baos = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() {
        pdDocumentStaticMock.close();
    }

    @Test
    public void testConvertToPDFA_Part2ConformanceB() throws Exception {
        String inputPath = "input.pdf";
        String outputPath = "output.pdf";
        PDFAStandard pdfaStandard = PDFAStandard.PDFA_2b;

        pdDocumentStaticMock.when(() -> PDDocument.load(new File(inputPath))).thenReturn(pdDocumentMock);

        PDFConverter converter = new PDFBoxConverter();
        converter.convertToPDFA(inputPath, outputPath, pdfaStandard);

        // Verify that the document was loaded and saved
        pdDocumentStaticMock.verify(() -> PDDocument.load(new File(inputPath)), times(1));
        verify(pdDocumentMock, times(1)).save(outputPath);
        verify(pdDocumentMock, times(1)).close();
    }

    @Test
    public void testConvertToPDFA_Part1ConformanceA() throws Exception {
        String inputPath = "input.pdf";
        String outputPath = "output.pdf";
        PDFAStandard pdfaStandard = PDFAStandard.PDFA_1a;

        pdDocumentStaticMock.when(() -> PDDocument.load(new File(inputPath))).thenReturn(pdDocumentMock);

        PDFConverter converter = new PDFBoxConverter();
        converter.convertToPDFA(inputPath, outputPath, pdfaStandard);

        // Verify that the document was loaded and saved
        pdDocumentStaticMock.verify(() -> PDDocument.load(new File(inputPath)), times(1));
        verify(pdDocumentMock, times(1)).save(outputPath);
        verify(pdDocumentMock, times(1)).close();
    }

    @Test
    public void testConvertToPDFA_InvalidPath() {
        String inputPath = "invalid_path.pdf";
        String outputPath = "output.pdf";
        PDFAStandard pdfaStandard = PDFAStandard.PDFA_1a;

        pdDocumentStaticMock.when(() -> PDDocument.load(new File(inputPath))).thenThrow(new IOException("File not found"));

        PDFConverter converter = new PDFBoxConverter();

        assertThrows(Exception.class, () -> {
            converter.convertToPDFA(inputPath, outputPath, pdfaStandard);
        });
    }

}
