package com.github.esslerc.pdfamaker.ui.widgets;

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
    private HBox pdfaStandardLayout;

    public PDFAStandardWidget(ResourceBundle i18n) {
        this.i18n = i18n;

        this.pdfaStandardChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                Arrays.stream(PDFAStandard.values()).map(PDFAStandard::getLabel).toList())
        );

        initWidget();
    }

    private void initWidget() {
        pdfaStandardLayout = new HBox(10);

        Label outputDirLabel = new Label(i18n.getString("pdfa_standard"));
        pdfaStandardLayout.getChildren().add(outputDirLabel);

        pdfaStandardChoiceBox.setStyle("-fx-z");
        pdfaStandardChoiceBox.setValue(PDFAStandard.PDFA_2b.getLabel());
        pdfaStandardLayout.getChildren().add(pdfaStandardChoiceBox);
    }

    public Pane getWidget() {
        return pdfaStandardLayout;
    }

    public ChoiceBox<String> getPDFAStandardChoiceBox() {
        return pdfaStandardChoiceBox;
    }

}
