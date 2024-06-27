package com.github.esslerc.pdfamaker;

import com.github.esslerc.pdfamaker.converters.PDFConverter;
import com.github.esslerc.pdfamaker.converters.PDFBoxConverter;
import com.github.esslerc.pdfamaker.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class PdfAMaker extends Application {
    @Override
    public void start(Stage primaryStage) {
        PDFConverter converter = new PDFBoxConverter();
        new MainWindow(converter, primaryStage);
        primaryStage.show();
    }

    public static void startApp(String[] args) {
        launch(args);
    }
}