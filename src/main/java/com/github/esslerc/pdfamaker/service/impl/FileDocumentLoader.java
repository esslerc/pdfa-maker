package com.github.esslerc.pdfamaker.service.impl;

import com.github.esslerc.pdfamaker.service.DocumentLoader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public class FileDocumentLoader implements DocumentLoader {
    @Override
    public PDDocument load(String path) throws IOException {
        return PDDocument.load(new File(path));
    }
}
