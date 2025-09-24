package com.github.esslerc.pdfamaker.service.impl;

import com.github.esslerc.pdfamaker.domain.PDFAStandard;
import com.github.esslerc.pdfamaker.service.PDFAServiceTestEnv;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;

public class PDFAServiceTest {

    @Test
    public void testConvertToPDFA_LoadsDocument() throws Exception {
        PDFAServiceTestEnv env = new PDFAServiceTestEnv.Builder().defaults().build();

        env.getPdfaService().convertToPDFA(
                env.getInputPath(),
                env.getOutputPath(),
                PDFAStandard.PDFA_1b
        );

        verify(env.getDocumentLoader()).load(env.getInputPath());
    }

    @Test
    public void testConvertToPDFA_SetsPDFAIdentification() throws Exception {

        PDFAServiceTestEnv env = new PDFAServiceTestEnv.Builder().defaults().build();

        env.getPdfaService().convertToPDFA(
                env.getInputPath(),
                env.getOutputPath(),
                PDFAStandard.PDFA_1a);

        verify(env.getPdfaIdSchema()).setPart(1);
        verify(env.getPdfaIdSchema()).setConformance("A");
    }

}
