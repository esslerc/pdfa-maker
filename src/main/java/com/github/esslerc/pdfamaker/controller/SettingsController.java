package com.github.esslerc.pdfamaker.controller;

import com.github.esslerc.pdfamaker.config.AppConfig;
import com.github.esslerc.pdfamaker.config.ConfigService;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingsController {

    private final ConfigService configService;
    private final ResourceBundle i18n;

    @FXML
    private ChoiceBox<String> pdfaStandardChoiceBox;

    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private TextField outputDirectoryField;

    @FXML
    private Button openFileDialogButton;

    @FXML
    private Button saveSettingsButton;


    public SettingsController(ConfigService configService, ResourceBundle i18n) {
        this.configService = configService;
        this.i18n = i18n;
    }


    @FXML
    private void initialize() {
        String buttonIcon = Objects.requireNonNull(getClass().getResource("/icons/heroicons/folder-open.png"))
                .toExternalForm();
        ImageView folderOpenIcon = new ImageView(buttonIcon);
        openFileDialogButton.setGraphic(folderOpenIcon);

        this.pdfaStandardChoiceBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(PDFAStandard.values()).map(PDFAStandard::getLabel).toList())
        );

        this.languageChoiceBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(new Locale[]{Locale.GERMAN, Locale.ENGLISH}).map(Locale::getLanguage).toList()
        ));

        String language = configService.getAppConfig().getLocale() == null ? Locale.getDefault().getLanguage() : configService.getAppConfig().getLocale().getLanguage();
        languageChoiceBox.setValue(language);

        String pdfaStandard = configService.getAppConfig().getPdfaStandard() == null ? PDFAStandard.PDFA_2b.getLabel() : configService.getAppConfig().getPdfaStandard().getLabel();
        pdfaStandardChoiceBox.setValue(pdfaStandard);

        String outputDirectoryPath = configService.getAppConfig().getOutputPath() == null ? System.getProperty("user.home") : configService.getAppConfig().getOutputPath();
        outputDirectoryField.setText(outputDirectoryPath);
    }

    @FXML
    private void saveSettings() {
        String pdfaStandardValue = pdfaStandardChoiceBox.getSelectionModel().getSelectedItem();
        String outputPath = outputDirectoryField.getText();
        String language = languageChoiceBox.getSelectionModel().getSelectedItem();

        AppConfig appConfig = configService.getAppConfig();
        appConfig.setPdfaStandard(PDFAStandard.getEnumForValue(pdfaStandardValue));
        appConfig.setOutputPath(outputPath);
        appConfig.setLocale(Locale.of(language));

        configService.saveProperties();
        configService.savePropertiesFromBean();

        Stage windowStage = (Stage) saveSettingsButton.getScene().getWindow();
        windowStage.close();
    }

    @FXML
    private void openFileDialog() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(i18n.getString("choose_dest_folder"));
        File selectedDir = chooser.showDialog(outputDirectoryField.getScene().getWindow());
        if (selectedDir != null) {
            outputDirectoryField.setText(selectedDir.getAbsolutePath());
        }
    }
}

