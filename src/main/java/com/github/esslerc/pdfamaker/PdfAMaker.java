package com.github.esslerc.pdfamaker;

import com.github.esslerc.pdfamaker.ui.MainWindowController;
import com.github.esslerc.pdfamaker.service.DocumentLoader;
import com.github.esslerc.pdfamaker.service.DocumentSaver;
import com.github.esslerc.pdfamaker.service.SettingsService;
import com.github.esslerc.pdfamaker.service.XmpMetadataCreator;
import com.github.esslerc.pdfamaker.service.impl.DefaultXmpMetadataCreator;
import com.github.esslerc.pdfamaker.service.impl.FileDocumentLoader;
import com.github.esslerc.pdfamaker.service.impl.FileDocumentSaver;
import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PdfAMaker extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        DocumentLoader documentLoader = new FileDocumentLoader();
        DocumentSaver documentSaver = new FileDocumentSaver();
        XmpMetadataCreator xmpMetadataCreator = new DefaultXmpMetadataCreator();

        SettingsService settingsService = new SettingsService();

        Locale locale = settingsService.getSettingsData().locale() == null ? Locale.getDefault() : settingsService.getSettingsData().locale();
        ResourceBundle i18n = ResourceBundle.getBundle("messages", locale);

        PDFAService converter = new PDFAService(documentLoader, documentSaver, xmpMetadataCreator);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        MainWindowController controller = new MainWindowController(converter, primaryStage, settingsService, i18n, getHostServices());
        loader.setResources(i18n);
        loader.setController(controller);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        Screen primaryScreen = Screen.getPrimary();
        Rectangle2D primaryBounds = primaryScreen.getVisualBounds();

        primaryStage.setX(primaryBounds.getMinX());
        primaryStage.setY(primaryBounds.getMinY());

        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    public static void startApp(String[] args) {
        launch(args);
    }
}