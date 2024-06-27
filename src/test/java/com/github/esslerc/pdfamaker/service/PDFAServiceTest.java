package com.github.esslerc.pdfamaker.service;

import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PDFAServiceTest {

    @Mock
    private DocumentLoader documentLoader;

    @Mock
    private DocumentSaver documentSaver;

    @Mock
    private XmpMetadataCreator xmpMetadataCreator;


    private PDFAService pdfaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pdfaService = new PDFAService(documentLoader, documentSaver, xmpMetadataCreator);
    }

    @Test
    public void testConvertToPDFA_LoadsDocument() throws Exception {
        String inputPath = "input.pdf";
        String outputPath = "output.pdf";

        PDDocument document = mock(PDDocument.class);
        PDDocumentCatalog catalog = mock(PDDocumentCatalog.class);
        COSDocument cosDocument = mock(COSDocument.class);
        COSDictionary cosDictionary = mock(COSDictionary.class);
        PDMetadata pdMetadata = mock(PDMetadata.class);
        COSStream cosStream = mock(COSStream.class);

        when(pdMetadata.getCOSObject()).thenReturn(cosStream);
        when(document.getDocument()).thenReturn(cosDocument);
        when(cosDocument.getTrailer()).thenReturn(cosDictionary);
        when(document.getDocumentCatalog()).thenReturn(catalog);
        when(documentLoader.load(inputPath)).thenReturn(document);

        XMPMetadata xmpMetadata = mock(XMPMetadata.class);
        when(xmpMetadataCreator.create()).thenReturn(xmpMetadata);

        PDFAIdentificationSchema pdfaid = mock(PDFAIdentificationSchema.class);
        when(xmpMetadata.createAndAddPDFAIdentificationSchema()).thenReturn(pdfaid);

        pdfaService.convertToPDFA(inputPath, outputPath, PDFAStandard.PDFA_1b);

        verify(documentLoader).load(inputPath);
    }

    @Test
    public void testConvertToPDFA_SetsPDFAIdentification() throws Exception {
        String inputPath = "input.pdf";
        String outputPath = "output.pdf";
        PDFAStandard pdfaStandard = PDFAStandard.PDFA_2b;

        PDDocument document = mock(PDDocument.class);
        when(documentLoader.load(inputPath)).thenReturn(document);

        XMPMetadata xmpMetadata = mock(XMPMetadata.class);
        when(xmpMetadataCreator.create()).thenReturn(xmpMetadata);

        PDFAIdentificationSchema pdfaid = mock(PDFAIdentificationSchema.class);
        when(xmpMetadata.createAndAddPDFAIdentificationSchema()).thenReturn(pdfaid);

        pdfaService.convertToPDFA(inputPath, outputPath, pdfaStandard);

        verify(pdfaid).setPart(2);
        verify(pdfaid).setConformance("B");
    }

    @Test
    public void testConvertToPDFA_SerializesXMPMetadata() throws Exception {
        String inputPath = "input.pdf";
        String outputPath = "output.pdf";
        PDFAStandard pdfaStandard = PDFAStandard.PDFA_1b;

        PDDocument document = mock(PDDocument.class);
        when(documentLoader.load(inputPath)).thenReturn(document);

        XMPMetadata xmpMetadata = mock(XMPMetadata.class);
        when(xmpMetadataCreator.create()).thenReturn(xmpMetadata);

        PDFAIdentificationSchema pdfaid = mock(PDFAIdentificationSchema.class);
        when(xmpMetadata.createAndAddPDFAIdentificationSchema()).thenReturn(pdfaid);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmpSerializer serializer = mock(XmpSerializer.class);
        doAnswer(invocation -> {
            baos.write("dummy data".getBytes());
            return null;
        }).when(serializer).serialize(eq(xmpMetadata), any(ByteArrayOutputStream.class), eq(true));

        pdfaService.convertToPDFA(inputPath, outputPath, pdfaStandard);

        assertTrue(baos.size() > 0);
    }

    @Test
    public void testConvertToPDFA_SavesDocument() throws Exception {
        String inputPath = "input.pdf";
        String outputPath = "output.pdf";
        PDFAStandard pdfaStandard = PDFAStandard.PDFA_1b;

        PDDocument document = mock(PDDocument.class);
        when(documentLoader.load(inputPath)).thenReturn(document);

        XMPMetadata xmpMetadata = mock(XMPMetadata.class);
        when(xmpMetadataCreator.create()).thenReturn(xmpMetadata);

        PDFAIdentificationSchema pdfaid = mock(PDFAIdentificationSchema.class);
        when(xmpMetadata.createAndAddPDFAIdentificationSchema()).thenReturn(pdfaid);

        pdfaService.convertToPDFA(inputPath, outputPath, pdfaStandard);

        verify(documentSaver).save(document, outputPath);
    }
}
