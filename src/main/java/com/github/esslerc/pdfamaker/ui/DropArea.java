package com.github.esslerc.pdfamaker.ui;

import com.github.esslerc.pdfamaker.domain.FileStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class DropArea extends VBox {

    @FXML
    private TableView<FileStatus> dropAreaTable;

    @FXML
    private TableColumn<FileStatus, Path> fileNameColumn;

    @FXML
    private TableColumn<FileStatus, Boolean> statusColumn;

    private ResourceBundle i18n;

    public DropArea(ResourceBundle i18n) {
        this.i18n = i18n;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DropArea.fxml"), i18n);
        loader.setController(this);
        this.setPadding(new Insets(10,0,10,0));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        dropAreaTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        dropAreaTable.setOnDragOver(this::handleDragOver);
        dropAreaTable.setOnDragDropped(this::handleDragDropped);
        dropAreaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        statusColumn.setPrefWidth(40);
        fileNameColumn.prefWidthProperty().bind(
                dropAreaTable.widthProperty().subtract(statusColumn.getPrefWidth())
        );

        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().convertedProperty());
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else if (item) {
                    setText("✔");
                    setTextFill(Color.GREEN);
                } else {
                    setText("✘");
                    setTextFill(Color.RED);
                }
            }
        });


        this.getChildren().add(dropAreaTable);

        addContextMenu();
        addRemoveKeyAction();
    }

    public TableView<FileStatus> getDropAreaTable() {
        return dropAreaTable;
    }

    public ObservableList<FileStatus> getAllItems() {
        return dropAreaTable.getItems();
    }

    public ObservableList<FileStatus> getSelectedItems() {
        return dropAreaTable.getSelectionModel().getSelectedItems();
    }

    public void removeSelectedItems() {
        var selected = dropAreaTable.getSelectionModel().getSelectedItems();
        dropAreaTable.getItems().removeAll(selected);
    }


    private void addRemoveKeyAction() {
        dropAreaTable.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DELETE) {
                removeSelectedItems();
                event.consume();
            }
        });
    }

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem(i18n.getString("remove"));
        removeItem.setOnAction(e -> {
            Object selected = dropAreaTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                dropAreaTable.getItems().remove(selected);
            }
        });
        contextMenu.getItems().add(removeItem);

        dropAreaTable.setContextMenu(contextMenu);

        dropAreaTable.setOnContextMenuRequested(e -> {
            if (dropAreaTable.getSelectionModel().getSelectedItem() != null) {
                contextMenu.show(dropAreaTable, e.getScreenX(), e.getScreenY());
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
        var db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            Set<String> existingPaths = dropAreaTable.getItems().stream()
                    .map(item -> item.getFilePath().toString())
                    .collect(Collectors.toSet());

            List<FileStatus> addedFileStatusItems = new ArrayList<>();

            for (File file : db.getFiles()) {
                String filePath = file.getAbsolutePath();
                if (file.getName().toLowerCase().endsWith(".pdf") && !existingPaths.contains(filePath)) {
                    FileStatus fileStatus = new FileStatus(file.toPath());
                    dropAreaTable.getItems().add(fileStatus);
                    existingPaths.add(filePath);
                    addedFileStatusItems.add(fileStatus);
                    success = true;
                }
            }
            MultipleSelectionModel<FileStatus> selectionModel = dropAreaTable.getSelectionModel();
            selectionModel.clearSelection();
            addedFileStatusItems.forEach(selectionModel::select);
        }
        event.setDropCompleted(success);
        event.consume();
    }
}
