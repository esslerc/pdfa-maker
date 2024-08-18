package com.github.esslerc.pdfamaker.ui.widgets;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.io.File;
import java.util.ResourceBundle;

public class DropArea extends ListView<String> {

    private final ResourceBundle i18n;

    public DropArea(ResourceBundle i18n) {
        this.i18n = i18n;

        this.setOnDragOver(this::handleDragOver);
        this.setOnDragDropped(this::handleDragDropped);
        this.setStyle("-fx-border-color: #aaa; -fx-border-style: dashed; -fx-border-width: 2; -fx-background-color: #f0f0f0;");

        super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addContextMenu();
        addDeleteKeyAction();
    }

    private void addDeleteKeyAction() {
        super.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DELETE) {
                ObservableList<String> selectedItems = super.getSelectionModel().getSelectedItems();
                super.getItems().removeAll(selectedItems);
                event.consume();
            }
        });
    }

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem(i18n.getString("delete"));
        deleteMenuItem.setOnAction(_ -> {
            String selectedItem = super.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                super.getItems().remove(selectedItem);
            }
        });
        contextMenu.getItems().add(deleteMenuItem);

        super.setContextMenu(contextMenu);

        super.setOnContextMenuRequested(event -> {
            if (super.getSelectionModel().getSelectedItem() != null) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
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
                if (isPdfFile(file) && fileNotExists(file)) {
                    getItems().add(file.getAbsolutePath());
                    success = true;
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private static boolean isPdfFile(File file) {
        return file.getName().toLowerCase().endsWith(".pdf");
    }

    private boolean fileNotExists(File file) {
        return !getItems().contains(file.getAbsolutePath());
    }
}
