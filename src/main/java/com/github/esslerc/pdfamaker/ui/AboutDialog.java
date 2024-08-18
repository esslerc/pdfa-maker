package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.util.VersionUtil;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class AboutDialog {

    private final Stage dialogStage;
    private final ResourceBundle i18n;
    private final HostServices hostServices;

    public AboutDialog(ResourceBundle i18n, HostServices hostServices) {
        this.i18n = i18n;
        this.hostServices = hostServices;

        dialogStage = new Stage();

        init();
    }

    private void init() {
        try {
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle(i18n.getString("about"));

            BorderPane borderPane = new BorderPane();

            VBox titleVbox = new VBox();
            titleVbox.setPadding(new Insets(20));

            String appVersion = VersionUtil.getVersion();
            Label title = new Label("PDF/A-Maker - Version " + appVersion);
            titleVbox.getChildren().add(title);

            Hyperlink link = new Hyperlink("PDF/A-Maker on Github");
            link.setOnAction(_ -> {
                hostServices.showDocument("https://github.com/esslerc/pdfa-maker");
            });
            titleVbox.getChildren().add(link);

            borderPane.setTop(titleVbox);

            TextArea licenseArea = new TextArea();

            String licenseContent = Files.readString(Paths.get("LICENSE").toAbsolutePath());
            licenseArea.setWrapText(true);
            licenseArea.setEditable(false);
            licenseArea.setText(licenseContent);

            borderPane.setCenter(licenseArea);

            Scene scene = new Scene(borderPane, 640, 480);
            dialogStage.setScene(scene);
        } catch (Exception exception) {
            System.out.println("LICENSE file is absent");
        }

    }


    public void showAndWait() {
        dialogStage.showAndWait();
    }

}

