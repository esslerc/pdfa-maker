package com.github.esslerc.pdfamaker.service;

import org.apache.xmpbox.XMPMetadata;

import java.io.IOException;

public interface XmpMetadataCreator {
    XMPMetadata create() throws IOException;
}
