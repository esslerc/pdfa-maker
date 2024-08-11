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

import java.io.IOException;

import static org.mockito.Mockito.*;

public class PDFAServiceTestEnv {

    private final DocumentLoader documentLoader;
    private final DocumentSaver documentSaver;
    private final XmpMetadataCreator xmpMetadataCreator;
    private final PDFAService pdfaService;


    private final PDDocument pdDocument;
    private final PDDocumentCatalog pdDocumentCatalog;
    private final COSDocument cosDocument;
    private final COSDictionary cosDictionary;
    private final PDMetadata pdMetadata;
    private final COSStream cosStream;
    private final XMPMetadata xmpMetadata;
    private final PDFAIdentificationSchema pdfaIdSchema;

    private final String inputPath;
    private final String outputPath;

    private PDFAServiceTestEnv(Builder builder) {
        this.documentLoader = mock(DocumentLoader.class);
        this.documentSaver = mock(DocumentSaver.class);
        this.xmpMetadataCreator = mock(XmpMetadataCreator.class);

        this.pdDocument = builder.pdDocument;
        this.pdDocumentCatalog = builder.pdDocumentCatalog;
        this.cosDocument = builder.cosDocument;
        this.cosDictionary = builder.cosDictionary;
        this.pdMetadata = builder.pdMetadata;
        this.cosStream = builder.cosStream;
        this.xmpMetadata = builder.xmpMetadata;
        this.pdfaIdSchema = builder.pdfaIdSchema;
        this.inputPath = builder.inputPath;
        this.outputPath = builder.outputPath;

        when(pdMetadata.getCOSObject()).thenReturn(cosStream);
        when(pdDocument.getDocument()).thenReturn(cosDocument);
        when(cosDocument.getTrailer()).thenReturn(cosDictionary);
        when(pdDocument.getDocumentCatalog()).thenReturn(pdDocumentCatalog);
        try {
            when(documentLoader.load(inputPath)).thenReturn(pdDocument);
            when(xmpMetadataCreator.create()).thenReturn(xmpMetadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        when(xmpMetadata.createAndAddPDFAIdentificationSchema()).thenReturn(pdfaIdSchema);

        pdfaService = spy(new PDFAService(getDocumentLoader(), getDocumentSaver(), getXmpMetadataCreator()));
        doReturn(pdMetadata).when(pdfaService).getPDMetadata(pdDocument);
    }

    public PDDocument getPdDocument() {
        return pdDocument;
    }

    public PDDocumentCatalog getPdDocumentCatalog() {
        return pdDocumentCatalog;
    }

    public COSDocument getCosDocument() {
        return cosDocument;
    }

    public COSDictionary getCosDictionary() {
        return cosDictionary;
    }

    public PDMetadata getPdMetadata() {
        return pdMetadata;
    }

    public COSStream getCosStream() {
        return cosStream;
    }

    public XMPMetadata getXmpMetadata() {
        return xmpMetadata;
    }

    public PDFAIdentificationSchema getPdfaIdSchema() {
        return pdfaIdSchema;
    }

    public DocumentLoader getDocumentLoader() {
        return documentLoader;
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public DocumentSaver getDocumentSaver() {
        return documentSaver;
    }

    public XmpMetadataCreator getXmpMetadataCreator() {
        return xmpMetadataCreator;
    }

    public PDFAService getPdfaService() {
        return pdfaService;
    }

    public static class Builder {
        private PDDocument pdDocument;
        private PDDocumentCatalog pdDocumentCatalog;
        private COSDocument cosDocument;
        private COSDictionary cosDictionary;
        private PDMetadata pdMetadata;
        private COSStream cosStream;
        private XMPMetadata xmpMetadata;
        private PDFAIdentificationSchema pdfaIdSchema;
        private String inputPath;
        private String outputPath;

        public Builder defaults() {
            this.pdDocument = mock(PDDocument.class);
            this.pdDocumentCatalog = mock(PDDocumentCatalog.class);
            this.cosDocument = mock(COSDocument.class);
            this.cosDictionary = mock(COSDictionary.class);
            this.pdMetadata =  mock(PDMetadata.class);
            this.cosStream = mock(COSStream.class);
            this.xmpMetadata = mock(XMPMetadata.class);
            this.pdfaIdSchema = mock(PDFAIdentificationSchema.class);
            this.inputPath = "input.pdf";
            this.outputPath = "output.pdf";

            return this;
        }


        public Builder withPDDocument(PDDocument pdDocument) {
            this.pdDocument = pdDocument;
            return this;
        }

        public Builder withPDDocumentCatalog(PDDocumentCatalog pdDocumentCatalog) {
            this.pdDocumentCatalog = pdDocumentCatalog;
            return this;
        }

        public Builder withCOSDocument(COSDocument cosDocument) {
            this.cosDocument = cosDocument;
            return this;
        }

        public Builder withCOSDictionary(COSDictionary cosDictionary) {
            this.cosDictionary = cosDictionary;
            return this;
        }

        public Builder withPDMetadata(PDMetadata pdMetadata) {
            this.pdMetadata = pdMetadata;
            return this;
        }

        public Builder withCOSStream(COSStream cosStream) {
            this.cosStream = cosStream;
            return this;
        }

        public Builder withXMPMetadata(XMPMetadata xmpMetadata) {
            this.xmpMetadata = xmpMetadata;
            return this;
        }

        public Builder withPdfaIdSchema(PDFAIdentificationSchema pdfaIdSchema) {
            this.pdfaIdSchema = pdfaIdSchema;
            return this;
        }

        public Builder withInputPath(String inputPath) {
            this.inputPath = inputPath;
            return this;
        }

        public Builder withOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public PDFAServiceTestEnv build() {
            return new PDFAServiceTestEnv(this);
        }
    }

}
