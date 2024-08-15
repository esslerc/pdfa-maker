package com.github.esslerc.pdfamaker.ui.widgets;

import com.github.esslerc.pdfamaker.config.ConfigService;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.ResourceBundle;

public class PDFAStandardWidget implements PDFAAppWidget {

    private final ResourceBundle i18n;
    private final ChoiceBox<String> pdfaStandardChoiceBox;
    private final ConfigService configService;
    private HBox pdfaStandardLayout;

    public PDFAStandardWidget(ResourceBundle i18n,
                              ConfigService configService) {
        this.i18n = i18n;

        this.pdfaStandardChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                Arrays.stream(PDFAStandard.values()).map(PDFAStandard::getLabel).toList())
        );

        this.configService = configService;

        init();
    }

    private void init() {
        pdfaStandardLayout = new HBox(10);

        Label outputDirLabel = new Label(i18n.getString("pdfa_standard"));
        pdfaStandardLayout.getChildren().add(outputDirLabel);

        String pdfaStandard = configService.getAppConfig().getPdfaStandard() == null ? PDFAStandard.PDFA_2b.getLabel() : configService.getAppConfig().getPdfaStandard().getLabel();
        pdfaStandardChoiceBox.setValue(pdfaStandard);

        pdfaStandardLayout.getChildren().add(pdfaStandardChoiceBox);
    }

    public Pane getWidget() {
        return pdfaStandardLayout;
    }

    public ChoiceBox<String> getPDFAStandardChoiceBox() {
        return pdfaStandardChoiceBox;
    }

}
