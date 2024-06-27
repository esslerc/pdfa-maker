package com.github.esslerc.pdfamaker.converters;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.XmpSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class PDFBoxConverter implements PDFConverter {
    @Override
    public void convertToPDFA(String inputPath, String outputPath, PDFAStandard pdfaStandard) throws Exception {
        try (PDDocument document = PDDocument.load(new File(inputPath))) {
            XMPMetadata xmp = XMPMetadata.createXMPMetadata();
            PDFAIdentificationSchema pdfaid = xmp.createAndAddPDFAIdentificationSchema();
            pdfaid.setPart(pdfaStandard.getPart());
            if(pdfaStandard.getPart() < 3) {
                pdfaid.setConformance(pdfaStandard.getConformance());
            }

            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmp, baos, true);

            PDMetadata metadata = new PDMetadata(document);
            metadata.importXMPMetadata(baos.toByteArray());
            document.getDocumentCatalog().setMetadata(metadata);

            document.save(outputPath);
        }
    }
}
