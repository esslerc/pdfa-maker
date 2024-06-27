package com.github.esslerc.pdfamaker.service;

import java.util.Arrays;
import java.util.Objects;

public enum PDFAStandard {
    PDFA_1b(1, "B", "PDF/A-1B"),
    PDFA_1a(1, "A", "PDF/A-1A"),

    PDFA_2b(2, "B", "PDF/A-2B"),
    PDFA_2u(2, "U", "PDF/A-2U"),
    PDFA_2a(2, "A", "PDF/A-2A"),

    PDFA_3(3, null, "PDF/A-3"),

    PDFA_4(4, null, "PDF/A-4");


    private final String conformance;
    private final Integer part;
    private final String label;

    public static PDFAStandard getEnumForValue(String value) {
        return Arrays.stream(PDFAStandard.values()).filter(it -> Objects.equals(it.getLabel(), value)).findFirst().orElseThrow(IllegalStateException::new);
    }

    public String getConformance() {
        return conformance;
    }

    public Integer getPart() {
        return part;
    }

    public String getLabel() {
        return label;
    }


    PDFAStandard(Integer part, String conformance, String label) {
        this.conformance = conformance;
        this.part = part;
        this.label = label;

    }
}
