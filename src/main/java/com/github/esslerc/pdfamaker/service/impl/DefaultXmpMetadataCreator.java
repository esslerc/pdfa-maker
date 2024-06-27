package com.github.esslerc.pdfamaker.service.impl;

import com.github.esslerc.pdfamaker.service.XmpMetadataCreator;
import org.apache.xmpbox.XMPMetadata;

public class DefaultXmpMetadataCreator implements XmpMetadataCreator {
    @Override
    public XMPMetadata create() {
        return XMPMetadata.createXMPMetadata();
    }
}
