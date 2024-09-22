package com.github.esslerc.pdfamaker.controller;

import com.github.esslerc.pdfamaker.util.VersionUtil;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.*;
import java.util.stream.Collectors;

public class AboutController {

    private final HostServices hostServices;

    @FXML
    Label titleLbl;

    @FXML
    TextArea licenseTextArea;

    public AboutController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private void initialize() throws IOException {
        String appVersion = VersionUtil.getVersion();
        titleLbl.setText("PDF/A-Maker - Version " + appVersion);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("LICENSE");

        if (inputStream == null) {
            throw new FileNotFoundException("Credit file not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            licenseTextArea.setText(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        }

        inputStream.close();
    }

    @FXML
    private void openGithubLink() {
        hostServices.showDocument("https://github.com/esslerc/pdfa-maker");
    }
}

