package com.github.esslerc.pdfamaker.ui.widgets;

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
    private TextField outputDirField;
    private HBox outputDirLayout;

    public OutputDirectoryWidget(Stage stage, ResourceBundle i18n) {
        this.stage = stage;
        this.i18n = i18n;

        initWidget();
    }

    private void initWidget() {
        outputDirLayout = new HBox(10);

        Label outputDirLabel = new Label(i18n.getString("dest_folder"));
        outputDirLayout.getChildren().add(outputDirLabel);

        outputDirField = new TextField();
        String homeDirectory = System.getProperty("user.home");
        outputDirField.setText(homeDirectory);
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
