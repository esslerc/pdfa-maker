package com.github.esslerc.pdfamaker.ui.widgets;

import com.github.esslerc.pdfamaker.config.ConfigService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;
import java.util.ResourceBundle;

public class OutputDirectoryWidget implements PDFAAppWidget {

    private final Stage stage;
    private final ResourceBundle i18n;
    private final ConfigService configService;
    private TextField outputDirField;
    private HBox outputDirLayout;

    public OutputDirectoryWidget(Stage stage,
                                 ResourceBundle i18n,
                                 ConfigService configService
    ) {
        this.stage = stage;
        this.i18n = i18n;
        this.configService = configService;

        init();
    }

    private void init() {
        outputDirLayout = new HBox(10);

        Label outputDirLabel = new Label(i18n.getString("dest_folder"));
        outputDirLayout.getChildren().add(outputDirLabel);

        outputDirField = new TextField();

        String outputDirectoryPath = configService.getAppConfig().getOutputPath() == null ? System.getProperty("user.home") : configService.getAppConfig().getOutputPath();
        outputDirField.setText(outputDirectoryPath);

        outputDirLayout.getChildren().add(outputDirField);
        HBox.setHgrow(outputDirField, Priority.ALWAYS);

        Button outputDirButton = new Button();
        String buttonIcon = Objects.requireNonNull(getClass().getResource("/icons/heroicons/folder-open.png"))
                .toExternalForm();
        ImageView folderOpenIcon = new ImageView(buttonIcon);
        outputDirButton.setGraphic(folderOpenIcon);
        outputDirButton.setOnAction(_ -> selectOutputDir());
        outputDirLayout.getChildren().add(outputDirButton);
    }

    public Pane getWidget() {
        return outputDirLayout;
    }

    private void selectOutputDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(i18n.getString("choose_dest_folder"));
        File selectedDir = chooser.showDialog(stage);
        if (selectedDir != null) {
            outputDirField.setText(selectedDir.getAbsolutePath());
        }
    }

    public TextField getOutputDirectoryField() {
        return outputDirField;
    }
}
