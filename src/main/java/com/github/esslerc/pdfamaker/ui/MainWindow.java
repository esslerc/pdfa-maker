package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import com.github.esslerc.pdfamaker.ui.widgets.DropArea;
import com.github.esslerc.pdfamaker.util.DirectoryUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindow {
    private final PDFAService converter;
    private final Stage stage;
    private DropArea dropArea;
    private Label statusLabel;
    private Button convertButton;
    private TextField outputDirField;
    private ChoiceBox<String> standardChoiceBox;

    public MainWindow(PDFAService converter, Stage stage) {
        this.converter = converter;
        this.stage = stage;
        initUI();
    }

    private void initUI() {
        stage.setTitle("PDF/A Maker");

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(createMenuBar());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        initializeStatusLabel();
        layout.getChildren().add(statusLabel);

        initializeDropArea();
        layout.getChildren().add(dropArea);

        HBox convertButtonLayout = new HBox(10);
        initializeConvertButton();
        convertButtonLayout.getChildren().add(convertButton);

        layout.getChildren().add(getOutputDirLayout());
        layout.getChildren().add(getConvertionOptionsLayout());
        layout.getChildren().add(convertButtonLayout);

        mainPane.setCenter(layout);
        Scene scene = new Scene(mainPane, 800, 600);

        stage.setScene(scene);
    }

    private void initializeConvertButton() {
        convertButton = new Button("Konvertieren zu PDF/A");
        convertButton.setOnAction(e -> convertFiles());
        convertButton.setDisable(true);
    }

    private void initializeDropArea() {
        dropArea = new DropArea();
        dropArea.getItems().addListener((ListChangeListener.Change<? extends String> change)-> updateStatus());
    }

    private void initializeStatusLabel() {
        statusLabel = new Label("Ziehen Sie die zu konvertierenden PDF-Dateien in den nachfolgenden Bereich");
    }

    private static MenuBar createMenuBar() {
        MenuBar menubar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(actionEvent -> Platform.exit());
        menu.getItems().add(exitItem);
        menubar.getMenus().add(menu);

        String os = System.getProperty("os.name");
        if(os != null && os.startsWith("Mac"))
            menubar.setUseSystemMenuBar(true);
        return menubar;
    }

    private Pane getOutputDirLayout() {
        HBox outputDirLayout = new HBox(10);

        Label outputDirLabel = new Label("Zielverzeichnis:");
        outputDirLayout.getChildren().add(outputDirLabel);

        outputDirField = new TextField();
        String tmpdir = System.getProperty("java.io.tmpdir");
        outputDirField.setText(tmpdir);
        outputDirLayout.getChildren().add(outputDirField);
        HBox.setHgrow(outputDirField, Priority.ALWAYS);

        Button outputDirButton = new Button("^");
        outputDirButton.setOnAction(e -> selectOutputDir());
        outputDirLayout.getChildren().add(outputDirButton);

        return outputDirLayout;
    }

    private Pane getConvertionOptionsLayout() {
        HBox convertionOptionsLayout = new HBox(10);

        Label outputDirLabel = new Label("Zielformat: ");
        convertionOptionsLayout.getChildren().add(outputDirLabel);

        standardChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                Arrays.stream(PDFAStandard.values()).map(PDFAStandard::getLabel).toList())
        );
        standardChoiceBox.setStyle("-fx-z");
        standardChoiceBox.setValue(PDFAStandard.PDFA_2b.getLabel());
        convertionOptionsLayout.getChildren().add(standardChoiceBox);

        return convertionOptionsLayout;
    }

    private void updateStatus() {
        int total = dropArea.getItems().size();
        statusLabel.setText(total + " Dateien ausgewählt");
        convertButton.setDisable(total == 0);
    }

    private void selectOutputDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Zielverzeichnis wählen");
        File selectedDir = chooser.showDialog(stage);
        if (selectedDir != null) {
            outputDirField.setText(selectedDir.getAbsolutePath());
        }
    }

    private void convertFiles() {
        String outputDir = outputDirField.getText();

        if (dropArea.getItems().isEmpty()) {
            showAlert("Warnung", "Keine Dateien zum Konvertieren ausgewählt!");
            return;
        }

        if (outputDir == null) {
            showAlert("Warnung", "Kein Zielverzeichnis ausgewählt!");
            return;
        }

        DirectoryUtils.ensureDirExists(outputDir);

        List<String> convertedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (String inputPath : dropArea.getItems()) {
            String filename = new File(inputPath).getName();
            String outputPath = new File(outputDir, "pdfa2b_" + filename).getAbsolutePath();

            try {
                PDFAStandard standardSelection = PDFAStandard.getEnumForValue(standardChoiceBox.getValue());
                converter.convertToPDFA(inputPath, outputPath, standardSelection);
                convertedFiles.add(filename);
            } catch (Exception e) {
                failedFiles.add(filename + ": " + e.getMessage());
            }
        }

        String message = "Erfolgreich konvertiert: " + convertedFiles.size() + "\n";
        if (!failedFiles.isEmpty()) {
            message += "\nFehler bei " + failedFiles.size() + " Dateien:\n" + String.join("\n", failedFiles);
        }

        showAlert("Konvertierung abgeschlossen", message);
        dropArea.getItems().clear();
        statusLabel.setText("Ziehen Sie PDF-Dateien in die Liste unten");
        convertButton.setDisable(true);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}