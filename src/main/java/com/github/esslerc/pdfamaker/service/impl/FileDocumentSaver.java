package com.github.esslerc.pdfamaker.service.impl;

import com.github.esslerc.pdfamaker.service.DocumentSaver;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public class FileDocumentSaver implements DocumentSaver {
    @Override
    public void save(PDDocument document, String path) throws IOException {
        document.save(path);
    }
}
