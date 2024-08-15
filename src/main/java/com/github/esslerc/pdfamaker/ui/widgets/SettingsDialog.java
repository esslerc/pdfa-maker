package com.github.esslerc.pdfamaker.ui.widgets;

import com.github.esslerc.pdfamaker.config.AppConfig;
import com.github.esslerc.pdfamaker.config.ConfigService;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsDialog  {

    private final Stage dialogStage;
    private final ResourceBundle i18n;
    private final OutputDirectoryWidget outputDirectoryWidget;
    private final PDFAStandardWidget pdfaStandardWidget;
    private final LocaleWidget localeWidget;
    private final ConfigService configService;

    public SettingsDialog(ResourceBundle i18n) {
        this.i18n = i18n;
        this.configService = new ConfigService();

        dialogStage = new Stage();

        pdfaStandardWidget = new PDFAStandardWidget(i18n, configService);
        outputDirectoryWidget = new OutputDirectoryWidget(dialogStage, i18n, configService);
        localeWidget = new LocaleWidget(i18n, configService);

        initDialog();
    }

    private void initDialog() {

        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(i18n.getString("settings"));

        Button saveButton = new Button(i18n.getString("save"));
        saveButton.setOnAction(_ -> saveSettings());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(
                pdfaStandardWidget.getWidget(),
                outputDirectoryWidget.getWidget(),
                localeWidget.getWidget(),
                saveButton
        );

        Scene scene = new Scene(vbox, 480, 200);
        dialogStage.setScene(scene);

    }


    public void showAndWait() {
        dialogStage.showAndWait();
    }

    private void saveSettings() {
        String pdfaStandardValue = pdfaStandardWidget.getPDFAStandardChoiceBox().getSelectionModel().getSelectedItem();
        String outputPath = outputDirectoryWidget.getOutputDirectoryField().getText();
        String language = localeWidget.getLanguageChoiceBox().getSelectionModel().getSelectedItem();

        AppConfig appConfig = configService.getAppConfig();
        appConfig.setPdfaStandard(PDFAStandard.getEnumForValue(pdfaStandardValue));
        appConfig.setOutputPath(outputPath);
        appConfig.setLocale(Locale.of(language));

        configService.saveProperties();
        configService.savePropertiesFromBean();

        dialogStage.close();
    }
}

