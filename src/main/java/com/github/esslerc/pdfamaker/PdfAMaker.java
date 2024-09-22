package com.github.esslerc.pdfamaker;

import com.github.esslerc.pdfamaker.config.ConfigService;
import com.github.esslerc.pdfamaker.controller.MainController;
import com.github.esslerc.pdfamaker.service.DocumentLoader;
import com.github.esslerc.pdfamaker.service.DocumentSaver;
import com.github.esslerc.pdfamaker.service.StageService;
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
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class PdfAMaker extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        DocumentLoader documentLoader = new FileDocumentLoader();
        DocumentSaver documentSaver = new FileDocumentSaver();
        XmpMetadataCreator xmpMetadataCreator = new DefaultXmpMetadataCreator();
        ConfigService configService = new ConfigService();
        StageService stageService = new StageService();

        Locale locale = configService.getAppConfig().getLocale() == null ? Locale.getDefault() : configService.getAppConfig().getLocale();
        ResourceBundle i18n = ResourceBundle.getBundle("messages", locale);

        PDFAService converter = new PDFAService(documentLoader, documentSaver, xmpMetadataCreator);

        InputStream appIcon = Objects.requireNonNull(getClass().getResourceAsStream("/icons/heroicons/document-check.png"));
        primaryStage.getIcons().add(new Image(appIcon));
        primaryStage.setTitle(i18n.getString("app_title"));

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/main.fxml")), i18n);
        loader.setControllerFactory(_ -> new MainController(converter, configService, primaryStage, i18n, getHostServices(), stageService));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

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