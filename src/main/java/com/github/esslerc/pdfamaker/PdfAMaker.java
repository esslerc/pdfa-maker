package com.github.esslerc.pdfamaker;

import com.github.esslerc.pdfamaker.service.impl.DefaultXmpMetadataCreator;
import com.github.esslerc.pdfamaker.service.impl.FileDocumentLoader;
import com.github.esslerc.pdfamaker.service.impl.FileDocumentSaver;
import com.github.esslerc.pdfamaker.service.DocumentLoader;
import com.github.esslerc.pdfamaker.service.DocumentSaver;
import com.github.esslerc.pdfamaker.service.XmpMetadataCreator;
import com.github.esslerc.pdfamaker.service.impl.PDFAService;
import com.github.esslerc.pdfamaker.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class PdfAMaker extends Application {
    @Override
    public void start(Stage primaryStage) {
        DocumentLoader documentLoader = new FileDocumentLoader();
        DocumentSaver documentSaver = new FileDocumentSaver();
        XmpMetadataCreator xmpMetadataCreator = new DefaultXmpMetadataCreator();

        PDFAService converter = new PDFAService(documentLoader, documentSaver, xmpMetadataCreator);

        new MainWindow(converter, primaryStage);
        primaryStage.show();
    }

    public static void startApp(String[] args) {
        launch(args);
    }
}