package com.github.esslerc.pdfamaker.controller;

import com.github.esslerc.pdfamaker.config.ConfigService;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import com.github.esslerc.pdfamaker.service.StageService;
import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import com.github.esslerc.pdfamaker.ui.DropArea;
import com.github.esslerc.pdfamaker.util.DirectoryUtils;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;


public class MainController {

    private final PDFAService converter;
    private final ConfigService configService;
    private final HostServices hostServices;
    private final ResourceBundle i18n;
    private final StageService stageService;

    private final Stage stage;

    @FXML
    private MenuBar menuBar;

    @FXML
    private DropArea dropArea;

    @FXML
    private Label statusLabel;

    @FXML
    private Button addButton;

    @FXML
    private Button convertButton;

    @FXML
    private TextField outputDirectoryField;

    @FXML
    private ChoiceBox<String> pdfaStandardChoiceBox;

    @FXML
    private Button openFileDialogButton;

    public MainController(PDFAService converter,
                          ConfigService configService,
                          Stage stage,
                          ResourceBundle i18n,
                          HostServices hostServices,
                          StageService stageService
    ) {
        this.converter = converter;
        this.configService = configService;
        this.stage = stage;
        this.i18n = i18n;
        this.hostServices = hostServices;
        this.stageService = stageService;
    }

    @FXML
    private void initialize() {

        initMacOSMenuBar();

        initDropArea();
        initAdvancedOptions();
        initAddButton();
        initConvertButton();
    }

    private void initMacOSMenuBar() {
        if(isMacOS()) {
            menuBar.setUseSystemMenuBar(true);
        }
    }

    private boolean isMacOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }

    private void initDropArea() {
        dropArea.getItems().addListener((ListChangeListener.Change<? extends String> _)-> updateStatus());
    }

    private void initAdvancedOptions() {
        this.pdfaStandardChoiceBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(PDFAStandard.values()).map(PDFAStandard::getLabel).toList())
        );

        String outputDirectoryPath = configService.getAppConfig().getOutputPath() == null ? System.getProperty("user.home") : configService.getAppConfig().getOutputPath();
        outputDirectoryField.setText(outputDirectoryPath);

        String pdfaStandard = configService.getAppConfig().getPdfaStandard() == null ? PDFAStandard.PDFA_2b.getLabel() : configService.getAppConfig().getPdfaStandard().getLabel();
        pdfaStandardChoiceBox.setValue(pdfaStandard);

        String buttonIcon = Objects.requireNonNull(getClass().getResource("/icons/heroicons/folder-open.png"))
                .toExternalForm();
        ImageView folderOpenIcon = new ImageView(buttonIcon);
        openFileDialogButton.setGraphic(folderOpenIcon);

    }

    private void initAddButton() {
        String buttonIcon = Objects.requireNonNull(getClass().getResource("/icons/heroicons/add.png"))
                .toExternalForm();
        ImageView addIcon = new ImageView(buttonIcon);
        addButton.setGraphic(addIcon);
    }

    private void initConvertButton() {
        String buttonIcon = Objects.requireNonNull(getClass().getResource("/icons/heroicons/play.png"))
                .toExternalForm();
        ImageView playIcon = new ImageView(buttonIcon);
        convertButton.setGraphic(playIcon);
        convertButton.setDisable(true);
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void openFileDialog() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(i18n.getString("choose_dest_folder"));
        File selectedDir = chooser.showDialog(outputDirectoryField.getScene().getWindow());
        if (selectedDir != null) {
            outputDirectoryField.setText(selectedDir.getAbsolutePath());
        }
    }

    @FXML
    private void openSettingsWindow() {
        stageService.initModalStage(
                "/fxml/settings.fxml",
                new SettingsController(configService, i18n),
                i18n.getString("menu.settings"),
                i18n
        );
    }

    @FXML
    private void openAboutWindow() {
        stageService.initModalStage(
                "/fxml/about.fxml",
                new AboutController(hostServices),
                i18n.getString("menu.about"),
                i18n
        );
    }

    @FXML
    private void openCreditsDialog() {
        stageService.initModalStage(
                "/fxml/credits.fxml",
                null,
                i18n.getString("menu.credits"),
                i18n
        );
    }

    private void updateStatus() {
        int total = dropArea.getItems().size();

        if(total == 0) {
            statusLabel.setText(i18n.getString("drag_files_into_drop_area"));
        } else {
            statusLabel.setText(total + " " + i18n.getString("files_selected"));
        }
        convertButton.setDisable(total == 0);

    }

    @FXML
    private void addFiles() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(i18n.getString("select_pdf_files"));
        List<File> selectedFiles = chooser.showOpenMultipleDialog(stage);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            selectedFiles.forEach(file -> {
                String absolutePath = file.getAbsolutePath();
                if(!dropArea.getItems().contains(absolutePath)) {
                    dropArea.getItems().add(absolutePath);
                }
            });
        }
    }

    @FXML
    private void convertFiles() {
        String outputDir = outputDirectoryField.getText();
        String warning = i18n.getString("warning");

        if (dropArea.getItems().isEmpty()) {
            showAlert(warning, i18n.getString("no_files_selected_to_convert"));
            return;
        }

        if (outputDir == null) {
            showAlert(warning, i18n.getString("no_dest_folder_selected"));
            return;
        }

        if(DirectoryUtils.ensureDirExists(outputDir)) {

            List<String> convertedFiles = new ArrayList<>();
            List<String> failedFiles = new ArrayList<>();

            for (String inputPath : dropArea.getItems()) {
                String filename = new File(inputPath).getName();
                PDFAStandard standardSelection = PDFAStandard.getEnumForValue(pdfaStandardChoiceBox.getValue());
                String outputPath = new File(outputDir, standardSelection.getLabel()
                        .replace("/", "").replace("-", "") +"_" + filename).getAbsolutePath();

                try {

                    converter.convertToPDFA(inputPath, outputPath, standardSelection);
                    convertedFiles.add(filename);
                } catch (Exception e) {
                    failedFiles.add(filename + ": " + e.getMessage());
                }
            }

            String message = i18n.getString("converted_successfully") + ": " + convertedFiles.size() + "\n";
            if (!failedFiles.isEmpty()) {
                message += "\n"
                        + i18n.getString("error_in")
                        + " "
                        + failedFiles.size()
                        + " "
                        + i18n.getString("files")
                        + ":\n"
                        + String.join("\n", failedFiles);
            }

            showAlert(i18n.getString("convertion_finished"), message);
            dropArea.getItems().clear();
            statusLabel.setText(i18n.getString("drag_files_into_drop_area"));
            convertButton.setDisable(true);
        } else {
            throw new IllegalArgumentException("Selected output directory is not accessible. Output directory: " + outputDir);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}