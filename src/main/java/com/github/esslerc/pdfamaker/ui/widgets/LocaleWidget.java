package com.github.esslerc.pdfamaker.ui.widgets;

import com.github.esslerc.pdfamaker.config.ConfigService;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleWidget implements PDFAAppWidget {

    private final ResourceBundle i18n;
    private final ChoiceBox<String> languageChoiceBox;
    private final ConfigService configService;
    private HBox languageLayout;


    public LocaleWidget(ResourceBundle i18n, ConfigService configService) {
        this.i18n = i18n;
        this.configService = configService;

        this.languageChoiceBox = new ChoiceBox<>(
            FXCollections.observableArrayList(
                Arrays.stream(new Locale[]{Locale.GERMAN, Locale.ENGLISH}).map(Locale::getLanguage).toList()
            )
        );

        init();
    }

    private void init() {
        languageLayout = new HBox(10);

        Label languageLabel = new Label(i18n.getString("language"));
        languageLayout.getChildren().add(languageLabel);

        String language = configService.getAppConfig().getLocale() == null ? Locale.getDefault().getLanguage() : configService.getAppConfig().getLocale().getLanguage();
        languageChoiceBox.setValue(language);

        languageLayout.getChildren().add(languageChoiceBox);
    }

    public Pane getWidget() {
        return languageLayout;
    }

    public ChoiceBox<String> getLanguageChoiceBox() {
        return languageChoiceBox;
    }

}
