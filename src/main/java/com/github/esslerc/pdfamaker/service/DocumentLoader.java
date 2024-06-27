package com.github.esslerc.pdfamaker.service;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public interface DocumentLoader {
    PDDocument load(String path) throws IOException;
}
