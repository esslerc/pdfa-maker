package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.domain.Credit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class CreditsDialogController extends AbstractI18nDialogController {

    @FXML
    private Label descriptionLabel;

    @FXML
    private ListView<Credit> licensesList;

    @FXML
    private TextArea licenseContentArea;

    private final Map<Credit, String> creditMap = new HashMap<>();

    public CreditsDialogController(ResourceBundle i18n) {
        super(i18n);
    }

    @FXML
    public void initialize() {
        descriptionLabel.setText(i18n.getString("licenseDialogDescription"));

        Arrays.stream(Credit.values()).forEach(credit -> {
            try {
                String licenseText = readLicenseFromFile(credit.getLicenseFileName());
                creditMap.put(credit, licenseText);
            } catch (Exception e) {
                creditMap.put(credit, i18n.getString("license_file_not_found"));
            }
        });

        licensesList.getItems().setAll(Credit.values());
        licensesList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            updateLicenseArea(selected);
        });

        licensesList.getSelectionModel().selectFirst();
        updateLicenseArea(licensesList.getSelectionModel().getSelectedItem());
    }

    private String readLicenseFromFile(String licenseFileName) throws Exception {
        InputStream stream = getClass().getResourceAsStream("/licenses/" + licenseFileName + ".txt");
        if (stream == null) {
            throw new IllegalStateException("License file not found: " + licenseFileName);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private void updateLicenseArea(Credit selected) {
        if (selected == null) {
            licenseContentArea.clear();
            return;
        }
        licenseContentArea.setText(creditMap.getOrDefault(selected, ""));
    }
}

