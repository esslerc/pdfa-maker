package com.github.esslerc.pdfamaker.service.impl;

import com.github.esslerc.pdfamaker.service.DocumentLoader;
import com.github.esslerc.pdfamaker.service.DocumentSaver;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import com.github.esslerc.pdfamaker.service.XmpMetadataCreator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.XmpSerializer;

import java.io.ByteArrayOutputStream;

public class PDFAService {
    private final DocumentLoader documentLoader;
    private final DocumentSaver documentSaver;
    private final XmpMetadataCreator xmpMetadataCreator;


    public PDFAService(DocumentLoader documentLoader,
                       DocumentSaver documentSaver,
                       XmpMetadataCreator xmpMetadataCreator
    ) {
        this.documentLoader = documentLoader;
        this.documentSaver = documentSaver;
        this.xmpMetadataCreator = xmpMetadataCreator;
    }

    public void convertToPDFA(String inputPath, String outputPath, PDFAStandard pdfaStandard) throws Exception {
        try (PDDocument document = documentLoader.load(inputPath)) {
            XMPMetadata xmp = xmpMetadataCreator.create();
            PDFAIdentificationSchema pdfaid = xmp.createAndAddPDFAIdentificationSchema();
            pdfaid.setPart(pdfaStandard.getPart());

            if (pdfaStandard.getPart() < 3) {
                pdfaid.setConformance(pdfaStandard.getConformance());
            }

            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmp, baos, true);

            PDMetadata metadata = new PDMetadata(document);
            metadata.importXMPMetadata(baos.toByteArray());
            document.getDocumentCatalog().setMetadata(metadata);

            documentSaver.save(document, outputPath);
        }
    }
}

