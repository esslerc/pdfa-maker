package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.domain.FileStatus;
import com.github.esslerc.pdfamaker.domain.PDFAStandard;
import com.github.esslerc.pdfamaker.service.SettingsData;
import com.github.esslerc.pdfamaker.service.SettingsService;
import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import com.github.esslerc.pdfamaker.util.DirectoryUtils;
import com.github.esslerc.pdfamaker.util.FXEventBus;
import com.github.esslerc.pdfamaker.util.SettingsSavedEvent;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class MainWindowController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem settingsMenuItem, exitMenuItem, creditsMenuItem, aboutMenuItem;

    @FXML
    private Label statusLabel;

    @FXML
    private Button addButton, removeButton, convertButton;

    @FXML
    private ImageView addIcon, playIcon, removeIcon;

    @FXML
    private TextField outputDirectoryField;

    @FXML
    private ComboBox<String> pdfaStandardChoiceBox;

    @FXML
    private VBox dropAreaBox;

    private DropArea dropArea;


    private final PDFAService converter;
    private final Stage stage;
    private final SettingsService settingsService;
    private final ResourceBundle i18n;
    private final HostServices hostServices;

    public MainWindowController(PDFAService converter,
                                Stage stage,
                                SettingsService settingsService,
                                ResourceBundle i18n,
                                HostServices hostServices) {
        this.converter = converter;
        this.stage = stage;
        this.settingsService = settingsService;
        this.i18n = i18n;
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize() {
        settingsService.setStageAppIcon(stage);

        settingsMenuItem.setOnAction(event -> openSettingsDialog());
        exitMenuItem.setOnAction(event -> Platform.exit());
        creditsMenuItem.setOnAction(event -> openLicensesDialog());
        aboutMenuItem.setOnAction(event -> openAboutDialog());

        if(isMacOS()) {
            menuBar.setUseSystemMenuBar(true);
        }

        addIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/heroicons/add.png"))));
        playIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/heroicons/play.png"))));
        removeIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/heroicons/cross_circle.png"))));

        addButton.setOnAction(event -> addFiles());
        removeButton.setOnAction(event -> removeFiles());
        removeButton.setDisable(true);
        convertButton.setOnAction(event -> convertFiles());
        convertButton.setDisable(true);

        dropArea = new DropArea(i18n);
        dropAreaBox.getChildren().add(dropArea);
        dropArea.getAllItems().addListener((ListChangeListener.Change<? extends FileStatus> c) -> updateStatus());
        dropArea.getDropAreaTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> removeButton.setDisable(newSel == null));

        outputDirectoryField.setText(settingsService.getSettingsData().outputPath());

        pdfaStandardChoiceBox.getItems().addAll(PDFAStandard.getPDFAStandardsAsStringList());
        pdfaStandardChoiceBox.getSelectionModel().select(settingsService.getSettingsData().pdfaStandard().getLabel());

        FXEventBus.subscribe(SettingsSavedEvent.class, this::onSettingsSaved);
    }

    private void onSettingsSaved(SettingsSavedEvent settingsSavedEvent) {
        SettingsData settingsData = settingsSavedEvent.newSettingsData();
        outputDirectoryField.setText(settingsData.outputPath());

        pdfaStandardChoiceBox.getItems().addAll(PDFAStandard.getPDFAStandardsAsStringList());
        pdfaStandardChoiceBox.getSelectionModel().select(settingsData.pdfaStandard().getLabel());
    }

    @FXML
    public void selectOutputDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(i18n.getString("choose_dest_folder"));
        File selectedDir = chooser.showDialog(stage);
        if (selectedDir != null) {
            outputDirectoryField.setText(selectedDir.getAbsolutePath());
        }
    }

    private void updateStatus() {
        int total = dropArea.getAllItems().size();
        if (total == 0) {
            statusLabel.setText(i18n.getString("drag_files_into_drop_area"));
        } else {
            statusLabel.setText(total + " " + i18n.getString("files_selected"));
        }
        convertButton.setDisable(total == 0);
    }

    private void addFiles() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(i18n.getString("select_pdf_files"));
        List<File> selectedFiles = chooser.showOpenMultipleDialog(stage);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            List<FileStatus> addedFileStatusItems = new ArrayList<>();
            for (File file : selectedFiles) {
                Path path = file.toPath();

                Set<Path> existingPaths = dropArea.getAllItems().stream()
                        .map(FileStatus::getFilePath)
                        .collect(Collectors.toSet());

                if (!existingPaths.contains(path)) {
                    FileStatus fileStatus = new FileStatus(path, null);
                    dropArea.getAllItems().add(fileStatus);
                    addedFileStatusItems.add(fileStatus);
                }
            }
            MultipleSelectionModel<FileStatus> selectionModel = dropArea.getDropAreaTable().getSelectionModel();
            selectionModel.clearSelection();
            addedFileStatusItems.forEach(selectionModel::select);
        }
    }

    private void removeFiles() {
        dropArea.removeSelectedItems();
    }

    private void convertFiles() {
        String outputDir = outputDirectoryField.getText();
        String warning = i18n.getString("warning");

        if (dropArea.getSelectedItems().isEmpty()) {
            showAlert(warning, i18n.getString("no_files_selected_to_convert"));
            return;
        }

        if (outputDir == null || outputDir.isBlank()) {
            showAlert(warning, i18n.getString("no_dest_folder_selected"));
            return;
        }

        if (DirectoryUtils.ensureDirExists(outputDir)) {
            List<FileStatus> convertedFiles = new ArrayList<>();
            List<FileStatus> failedFiles = new ArrayList<>();

            for (FileStatus filestatus : dropArea.getSelectedItems()) {
                try {
                    Path filePath = filestatus.getFilePath();
                    String filename = filePath.toFile().getName();
                    PDFAStandard pdfaStandard = settingsService.getSettingsData().pdfaStandard();

                    PDFAStandard selectedPDFAStandard = PDFAStandard.getEnumForValue(pdfaStandardChoiceBox.getSelectionModel().getSelectedItem());
                    if(pdfaStandard != selectedPDFAStandard) {
                        pdfaStandard = selectedPDFAStandard;
                    }

                    String outputPath = new File(outputDir, pdfaStandard.getLabel().replace("/","").toLowerCase() + "_" + filename).getAbsolutePath();

                    converter.convertToPDFA(filePath.toAbsolutePath().toString(), outputPath, pdfaStandard);
                    filestatus.setConverted(true);
                    convertedFiles.add(filestatus);
                } catch (Exception e) {
                    filestatus.setConverted(false);
                    failedFiles.add(filestatus);
                }
            }

            String message = i18n.getString("converted_successfully") + ": " + convertedFiles.size();
            if (!failedFiles.isEmpty()) {
                message += "\n\n" + i18n.getString("error_in") + " " + failedFiles.size() + " " + i18n.getString("files") + ":\n" + String.join("\n", failedFiles.stream().map(it -> it.getFilePath().toString()).toList());
            }

            showAlert(i18n.getString("convertion_finished"), message);
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

    private boolean isMacOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }

    private <T> void openDialog(String fxmlPath, String title, T controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        try {
            loader.setResources(i18n);
            if (controller != null) {
                loader.setController(controller);
            }
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.setScene(scene);
            dialogStage.show();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void openSettingsDialog() {
        SettingsDialogController controller = new SettingsDialogController(settingsService);
        openDialog("/fxml/SettingsDialog.fxml", i18n.getString("settings"), controller);
    }

    private void openLicensesDialog() {
        CreditsDialogController controller = new CreditsDialogController(i18n);
        openDialog("/fxml/CreditsDialog.fxml", i18n.getString("credits"), controller);
    }

    private void openAboutDialog() {
        AboutDialogController controller = new AboutDialogController(i18n, hostServices);
        openDialog("/fxml/AboutDialog.fxml", i18n.getString("about"), controller);
    }


}

