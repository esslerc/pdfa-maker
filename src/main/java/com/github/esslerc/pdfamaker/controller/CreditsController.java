package com.github.esslerc.pdfamaker.controller;

import com.github.esslerc.pdfamaker.service.Credit;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CreditsController {

    private Map<Credit, String> creditMap;

    @FXML
    ListView<Credit> creditListView;

    @FXML
    TextArea licenseArea;

    @FXML
    private void initialize() {
        this.creditMap = new HashMap<>();

        AtomicReference<Credit> firstCredit = new AtomicReference<>();

        Arrays.stream(Credit.values()).forEach(item -> {
                    if (firstCredit.get() == null) {
                        firstCredit.set(item);
                    }
                    String licenseContent;
                    try {
                        licenseContent = readLicenseFromFile(item.getLicenseFileName());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    creditMap.put(item, licenseContent);
                    creditListView.getItems().add(item);
                }
        );

        creditListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        creditListView.getSelectionModel().select(firstCredit.get());
        updateLicenseArea();
    }

    private String readLicenseFromFile(String licenseFileName) throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream("/licenses/" + licenseFileName + ".txt");

        if (resourceStream == null) {
            throw new IllegalStateException("License file not found!");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    @FXML
    private void updateLicenseArea() {
        Credit selectedCredit = creditListView.getSelectionModel().getSelectedItem();
        licenseArea.setText(creditMap.get(selectedCredit));
    }

}
