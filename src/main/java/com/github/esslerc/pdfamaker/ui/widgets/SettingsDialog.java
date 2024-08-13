package com.github.esslerc.pdfamaker.ui.widgets;

import com.github.esslerc.pdfamaker.config.AppConfig;
import com.github.esslerc.pdfamaker.config.ConfigService;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class SettingsDialog  {

    private final Stage dialogStage;
    private final ResourceBundle i18n;
    private final OutputDirectoryWidget outputDirectoryWidget;
    private final PDFAStandardWidget pdfaStandardWidget;
    private final ConfigService configService;

    public SettingsDialog(ResourceBundle i18n) {
        this.i18n = i18n;
        this.configService = new ConfigService();

        dialogStage = new Stage();

        pdfaStandardWidget = new PDFAStandardWidget(i18n);
        outputDirectoryWidget = new OutputDirectoryWidget(dialogStage, i18n);

        initDialog();
    }

    private void initDialog() {

        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(i18n.getString("settings"));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        GridPane.setConstraints(pdfaStandardWidget.getWidget(), 0, 0);
        GridPane.setConstraints(outputDirectoryWidget.getWidget(), 0, 1);

        Button saveButton = new Button("Speichern");
        saveButton.setOnAction(e -> saveSettings());

        GridPane.setConstraints(saveButton, 0, 2);

        gridPane.getChildren().addAll(pdfaStandardWidget.getWidget(), outputDirectoryWidget.getWidget(), saveButton);

        Scene scene = new Scene(gridPane, 640, 480);
        dialogStage.setScene(scene);

    }


    public void showAndWait() {
        dialogStage.showAndWait();
    }

    private void saveSettings() {
        String pdfaStandardValue = pdfaStandardWidget.getPDFAStandardChoiceBox().getSelectionModel().getSelectedItem();
        String outputPath = outputDirectoryWidget.getOutputDirectoryField().getText();

        AppConfig appConfig = configService.getAppConfig();
        appConfig.setPdfaStandard(PDFAStandard.getEnumForValue(pdfaStandardValue));
        appConfig.setOutputPath(outputPath);

        configService.saveProperties();
        configService.savePropertiesFromBean();

        dialogStage.close();
    }
}

