package com.github.esslerc.pdfamaker.service;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class StageService {

     public void initModalStage(String fxmlFile, Object controller, String title, ResourceBundle i18n) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile), i18n);
            if(controller != null) {
                fxmlLoader.setControllerFactory(_ -> controller);
            }
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root);
            stage.setScene(scene);

            Screen primaryScreen = Screen.getPrimary();
            Rectangle2D primaryBounds = primaryScreen.getVisualBounds();

            stage.setX(primaryBounds.getMinX());
            stage.setY(primaryBounds.getMinY());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
