package com.github.esslerc.pdfamaker.service;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public interface DocumentSaver {
    void save(PDDocument document, String path) throws IOException;
}



