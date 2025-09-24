package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.util.VersionUtil;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AboutDialogController extends AbstractI18nDialogController {

    @FXML
    private Label titleLabel;

    @FXML
    private Hyperlink githubLink;

    @FXML
    private Text descriptionText;

    @FXML
    private TextArea licenseArea;

    private HostServices hostServices;

    AboutDialogController(ResourceBundle i18n, HostServices hostServices) {
        super(i18n);
        this.hostServices = hostServices;

    }

    @FXML
    private void initialize() {
        String appVersion = VersionUtil.getVersion();
        titleLabel.setText("PDF/A-Maker - Version " + appVersion);

        githubLink.setOnAction(evt -> hostServices.showDocument("https://github.com/esslerc/pdfa-maker"));

        descriptionText.setText(i18n.getString("aboutContent"));

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("LICENSE");
        if (inputStream == null) {
            licenseArea.setText(i18n.getString("license_file_not_found"));
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            licenseArea.setText(reader.lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (Exception e) {
            licenseArea.setText(i18n.getString("license_read_error"));
        }
    }
}
