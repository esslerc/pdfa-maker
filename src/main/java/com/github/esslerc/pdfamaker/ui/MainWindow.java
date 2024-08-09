package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.service.PDFAStandard;
import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import com.github.esslerc.pdfamaker.ui.widgets.DropArea;
import com.github.esslerc.pdfamaker.util.DirectoryUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class MainWindow {
    private final ResourceBundle i18n;
    private final PDFAService converter;
    private final Stage stage;
    private DropArea dropArea;
    private Label statusLabel;
    private Button convertButton;
    private TextField outputDirField;
    private ChoiceBox<String> standardChoiceBox;

    public MainWindow(PDFAService converter, Stage stage, ResourceBundle i18n) {
        this.converter = converter;
        this.stage = stage;
        this.i18n = i18n;

        initUI();
    }

    private void initUI() {
        stage.setTitle(i18n.getString("app_title"));

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
        String appCss = Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm();
        scene.getStylesheets().add(appCss);

        stage.setScene(scene);
    }

    private void initializeConvertButton() {
        convertButton = new Button(i18n.getString("convert_to_pdfa"));
        convertButton.setOnAction(e -> convertFiles());
        convertButton.setDisable(true);
    }

    private void initializeDropArea() {
        dropArea = new DropArea();
        dropArea.getItems().addListener((ListChangeListener.Change<? extends String> change)-> updateStatus());
    }

    private void initializeStatusLabel() {
        statusLabel = new Label(i18n.getString("drag_files_into_drop_area"));
    }

    private MenuBar createMenuBar() {
        MenuBar menubar = new MenuBar();
        Menu menu = new Menu(i18n.getString("file"));
        MenuItem exitItem = new MenuItem(i18n.getString("exit"));
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

        Label outputDirLabel = new Label(i18n.getString("dest_folder"));
        outputDirLayout.getChildren().add(outputDirLabel);

        outputDirField = new TextField();
        String tmpdir = System.getProperty("java.io.tmpdir");
        outputDirField.setText(tmpdir);
        outputDirLayout.getChildren().add(outputDirField);
        HBox.setHgrow(outputDirField, Priority.ALWAYS);

        Button outputDirButton = new Button();
        ImageView folderOpenIcon = new ImageView(getClass().getResource("/icons/heroicons/folder-open.png").toExternalForm());
        outputDirButton.setGraphic(folderOpenIcon);
        outputDirButton.setOnAction(e -> selectOutputDir());
        outputDirLayout.getChildren().add(outputDirButton);

        return outputDirLayout;
    }

    private Pane getConvertionOptionsLayout() {
        HBox convertionOptionsLayout = new HBox(10);

        Label outputDirLabel = new Label(i18n.getString("dest_format"));
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
        statusLabel.setText(total + " " + i18n.getString("files_selected"));
        convertButton.setDisable(total == 0);
    }

    private void selectOutputDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(i18n.getString("choose_dest_folder"));
        File selectedDir = chooser.showDialog(stage);
        if (selectedDir != null) {
            outputDirField.setText(selectedDir.getAbsolutePath());
        }
    }

    private void convertFiles() {
        String outputDir = outputDirField.getText();
        String warning = i18n.getString("warning");

        if (dropArea.getItems().isEmpty()) {
            showAlert(warning, i18n.getString("no_files_selected_to_convert"));
            return;
        }

        if (outputDir == null) {
            showAlert(warning, i18n.getString("no_dest_folder_selected"));
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

        String message = i18n.getString("converted_successfully")+": " + convertedFiles.size() + "\n";
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
        statusLabel.setText(i18n.getString("drag_files_into_list"));
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