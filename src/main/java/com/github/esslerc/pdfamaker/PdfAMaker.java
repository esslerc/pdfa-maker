package com.github.esslerc.pdfamaker;

import com.github.esslerc.pdfamaker.service.DocumentLoader;
import com.github.esslerc.pdfamaker.service.DocumentSaver;
import com.github.esslerc.pdfamaker.service.XmpMetadataCreator;
import com.github.esslerc.pdfamaker.service.impl.DefaultXmpMetadataCreator;
import com.github.esslerc.pdfamaker.service.impl.FileDocumentLoader;
import com.github.esslerc.pdfamaker.service.impl.FileDocumentSaver;
import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import com.github.esslerc.pdfamaker.ui.MainWindow;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class PdfAMaker extends Application {
    @Override
    public void start(Stage primaryStage) {
        DocumentLoader documentLoader = new FileDocumentLoader();
        DocumentSaver documentSaver = new FileDocumentSaver();
        XmpMetadataCreator xmpMetadataCreator = new DefaultXmpMetadataCreator();

        Locale locale = new Locale("de", "DE");
        ResourceBundle i18n = ResourceBundle.getBundle("messages", locale);


        PDFAService converter = new PDFAService(documentLoader, documentSaver, xmpMetadataCreator);

        new MainWindow(converter, primaryStage, i18n);

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