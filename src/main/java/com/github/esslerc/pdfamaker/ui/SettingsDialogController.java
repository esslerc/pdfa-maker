package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.domain.PDFAStandard;
import com.github.esslerc.pdfamaker.service.SettingsData;
import com.github.esslerc.pdfamaker.service.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class SettingsDialogController {

    @FXML
    private ComboBox<String> pdfaStandardComboBox;

    @FXML
    private TextField directoryField;

    @FXML
    private Button chooseFolderBtn;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private Button saveBtn;

    private final SettingsService settingsService;
    private final DirectoryChooser directoryChooser;

    public SettingsDialogController(SettingsService settingsService) {
        this.settingsService = settingsService;
        this.directoryChooser = new DirectoryChooser();
    }

    @FXML
    public void initialize() {
        pdfaStandardComboBox.getItems().addAll(PDFAStandard.getPDFAStandardsAsStringList());
        languageComboBox.getItems().addAll(List.of(Locale.GERMAN.getLanguage(), Locale.ENGLISH.getLanguage()));

        loadSettings();

        chooseFolderBtn.setOnAction(event -> openDirectoryChooser());
        saveBtn.setOnAction(event -> saveSettings());
    }

    private void loadSettings() {
        SettingsData settingsData = settingsService.getSettingsData();
        pdfaStandardComboBox.setValue(settingsData.pdfaStandard().getLabel());
        directoryField.setText(settingsData.outputPath());
        languageComboBox.setValue(settingsData.locale().getLanguage());
    }

    private void openDirectoryChooser() {
        Window window = chooseFolderBtn.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(window);
        if (selectedDirectory != null) {
            directoryField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void saveSettings() {
        String directory = directoryField.getText();
        PDFAStandard pdfaStandard = PDFAStandard.getEnumForValue(pdfaStandardComboBox.getValue());
        Locale language = Locale.of(languageComboBox.getValue());

        if (isValid(directory, pdfaStandard, language)) {
            SettingsData newSettingsData = new SettingsData(directory, pdfaStandard, language);
            settingsService.saveSettingsData(newSettingsData);
        }

        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

    private boolean isValid(String directory, PDFAStandard pdfaStandard, Locale language) {
        return pdfaStandard != null
                && directory != null && !directory.isEmpty()
                && language != null;
    }
}
