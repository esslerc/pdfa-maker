package com.github.esslerc.pdfamaker.ui.credits;

public enum Credit {
    APACHE_PDFBOX("Apache PDFBox", "Apache_License_2.0"),
    APACHE_MAVEN("Apache Maven", "Apache_License_2.0"),
    JUNIT("JUnit5", "EPL-2.0"),
    MOCKITO("mockito", "MIT_mockito"),
    ECLIPSE_TEMURIN("Eclipse Temurin", "GPL_with_Classpath_Exception");

    private final String creditName;
    private final String licenseFileName;

    Credit(String creditName, String licenseFileName) {
        this.creditName = creditName;
        this.licenseFileName = licenseFileName;
    }

    public String getCreditName() {
        return creditName;
    }

    public String getLicenseFileName() {
        return licenseFileName;
    }

    @Override
    public String toString() {
        return getCreditName();
    }
}
