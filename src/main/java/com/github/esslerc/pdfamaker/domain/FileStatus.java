package com.github.esslerc.pdfamaker.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.nio.file.Path;

public class FileStatus {
    private final Path filePath;
    private final ObjectProperty<Boolean> converted = new SimpleObjectProperty<>(null);

    public FileStatus(Path filePath) {
        this.filePath = filePath;
    }

    public FileStatus(Path filePath, Boolean converted) {
        this.filePath = filePath;
        this.converted.set(converted);
    }

    public Path getFilePath() {
        return filePath;
    }

    public ObjectProperty<Boolean> convertedProperty() {
        return converted;
    }

    public Boolean getConverted() {
        return converted.get();
    }

    public void setConverted(Boolean value) {
        converted.set(value);
    }
}
