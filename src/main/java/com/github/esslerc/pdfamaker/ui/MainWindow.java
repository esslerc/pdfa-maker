package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.config.ConfigService;
import com.github.esslerc.pdfamaker.service.PDFAStandard;
import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import com.github.esslerc.pdfamaker.ui.credits.CreditsDialog;
import com.github.esslerc.pdfamaker.ui.widgets.*;
import com.github.esslerc.pdfamaker.util.DirectoryUtils;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainWindow {
    private final ResourceBundle i18n;
    private final PDFAService converter;
    private final ConfigService configService;
    private final Stage stage;
    private final HostServices hostServices;
    private DropArea dropArea;
    private Label statusLabel;
    private Button addButton;
    private Button convertButton;
    private OutputDirectoryWidget outputDirectoryWidget;
    private PDFAStandardWidget pdfaStandardWidget;

    public MainWindow(PDFAService converter,
                      ConfigService configService,
                      Stage stage,
                      ResourceBundle i18n,
                      HostServices hostServices
    ) {
        this.converter = converter;
        this.configService = configService;
        this.stage = stage;
        this.i18n = i18n;
        this.hostServices = hostServices;

        init();
    }

    private void init() {
        stage.setTitle(i18n.getString("app_title"));

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(createMenuBar());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        initializeStatusLabel();
        layout.getChildren().add(statusLabel);

        initializeDropArea();
        layout.getChildren().add(dropArea);

        HBox buttonLayout = new HBox(10);
        initializeAddButton();
        initializeConvertButton();
        buttonLayout.getChildren().add(addButton);
        buttonLayout.getChildren().add(convertButton);

        layout.getChildren().add(buttonLayout);

        VBox advancedOptionsLayout = new VBox(10);

        outputDirectoryWidget = new OutputDirectoryWidget(stage, i18n, configService);
        advancedOptionsLayout.getChildren().add(outputDirectoryWidget.getWidget());

        pdfaStandardWidget = new PDFAStandardWidget(i18n, configService);
        advancedOptionsLayout.getChildren().add(pdfaStandardWidget.getWidget());

        TitledPane optionsPane = new TitledPane(i18n.getString("advanced_options"), advancedOptionsLayout);
        optionsPane.setExpanded(false);

        layout.getChildren().add(optionsPane);

        mainPane.setCenter(layout);
        Scene scene = new Scene(mainPane);
        String appCss = Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm();
        scene.getStylesheets().add(appCss);

        stage.setScene(scene);
    }

    private void initializeAddButton() {
        addButton = new Button(i18n.getString("add"));
        String buttonIcon = Objects.requireNonNull(getClass().getResource("/icons/heroicons/add.png"))
                .toExternalForm();
        ImageView addIcon = new ImageView(buttonIcon);
        addButton.setGraphic(addIcon);
        addButton.setOnAction(_ -> addFiles());
    }

    private void initializeConvertButton() {
        convertButton = new Button(i18n.getString("convert_to_pdfa"));
        String buttonIcon = Objects.requireNonNull(getClass().getResource("/icons/heroicons/play.png"))
                .toExternalForm();
        ImageView playIcon = new ImageView(buttonIcon);
        convertButton.setGraphic(playIcon);
        convertButton.setOnAction(_ -> convertFiles());
        convertButton.setDisable(true);
    }

    private void initializeDropArea() {
        dropArea = new DropArea(i18n);
        dropArea.getItems().addListener((ListChangeListener.Change<? extends String> _)-> updateStatus());
    }

    private void initializeStatusLabel() {
        statusLabel = new Label(i18n.getString("drag_files_into_drop_area"));
    }

    private MenuBar createMenuBar() {
        MenuBar menubar = new MenuBar();

        menubar.getMenus().add(createFileMenu());
        menubar.getMenus().add(createHelpMenu());

        if(isMacOS()) {
            menubar.setUseSystemMenuBar(true);
        }

        return menubar;
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu(i18n.getString("file"));

        MenuItem settingsItem = new MenuItem(i18n.getString("settings"));
        settingsItem.setOnAction(_ -> openSettingsDialog());

        MenuItem exitItem = new MenuItem(i18n.getString("exit"));
        exitItem.setOnAction(_ -> Platform.exit());

        fileMenu.getItems().add(settingsItem);
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(exitItem);
        return fileMenu;
    }

    private Menu createHelpMenu() {
        Menu helpMenu = new Menu(i18n.getString("help"));

        MenuItem licenseItem = new MenuItem(i18n.getString("credits"));
        licenseItem.setOnAction(_ -> openLicensesDialog());
        helpMenu.getItems().add(licenseItem);

        MenuItem aboutItem = new MenuItem(i18n.getString("about"));
        aboutItem.setOnAction(_ -> openAboutDialog());
        helpMenu.getItems().add(aboutItem);

        return helpMenu;
    }

    private boolean isMacOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }

    private void openAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(i18n, hostServices);
        aboutDialog.showAndWait();
    }

    private void openSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog(i18n);
        settingsDialog.showAndWait();

    }

    private void openLicensesDialog() {
        CreditsDialog creditsDialog = new CreditsDialog(i18n);
        creditsDialog.showAndWait();

    }

    private void updateStatus() {
        int total = dropArea.getItems().size();

        if(total == 0) {
            statusLabel.setText(i18n.getString("drag_files_into_drop_area"));
        } else {
            statusLabel.setText(total + " " + i18n.getString("files_selected"));
        }
        convertButton.setDisable(total==0);

    }

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

    private void convertFiles() {
        String outputDir = outputDirectoryWidget.getOutputDirectoryField().getText();
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
                String outputPath = new File(outputDir, "pdfa2b_" + filename).getAbsolutePath();

                try {
                    PDFAStandard standardSelection = PDFAStandard.getEnumForValue(pdfaStandardWidget.getPDFAStandardChoiceBox().getValue());
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