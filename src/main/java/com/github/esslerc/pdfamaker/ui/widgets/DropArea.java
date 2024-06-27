package com.github.esslerc.pdfamaker.ui.widgets;

import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;

public class DropArea extends ListView<String> {
    public DropArea() {
        this.setOnDragOver(this::handleDragOver);
        this.setOnDragDropped(this::handleDragDropped);
        this.setStyle("-fx-border-color: #aaa; -fx-border-style: dashed; -fx-border-width: 2; -fx-background-color: #f0f0f0;");
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            for (File file : db.getFiles()) {
                if (file.getName().toLowerCase().endsWith(".pdf")) {
                    getItems().add(file.getAbsolutePath());
                    success = true;
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }
}
