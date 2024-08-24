package com.github.esslerc.pdfamaker.ui.credits;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CreditsDialog {

    private final Stage dialogStage;
    private final ResourceBundle i18n;
    private final Map<Credit, String> creditMap;

    private TextArea licenseContent;

    public CreditsDialog(ResourceBundle i18n) {
        this.i18n = i18n;

        this.creditMap = new HashMap<>();

        dialogStage = new Stage();

        init();
    }

    private void init() {

        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(i18n.getString("credits"));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label description = new Label(i18n.getString("licenseDialogDescription"));
        vbox.getChildren().add(description);

        HBox hbox = new HBox();

        ListView<Credit> licensesList = new ListView<>();
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
                licensesList.getItems().add(item);
            }
        );
        licensesList.setOnMouseClicked(_ -> updateLicenseArea(licensesList.getSelectionModel().getSelectedItem()));
        hbox.getChildren().add(licensesList);

        licensesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        licensesList.getSelectionModel().select(firstCredit.get());

        licenseContent = new TextArea();
        licenseContent.setEditable(false);
        licenseContent.setWrapText(true);
        hbox.getChildren().add(licenseContent);

        vbox.getChildren().add(hbox);

        Scene scene = new Scene(vbox, 640, 480);
        dialogStage.setScene(scene);

        updateLicenseArea(firstCredit.get());

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

    private void updateLicenseArea(Credit item) {
        licenseContent.setText(creditMap.get(item));
    }


    public void showAndWait() {
        dialogStage.showAndWait();
    }

}

