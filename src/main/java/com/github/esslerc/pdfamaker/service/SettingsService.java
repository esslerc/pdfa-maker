package com.github.esslerc.pdfamaker.service;

import com.github.esslerc.pdfamaker.domain.PDFAStandard;
import com.github.esslerc.pdfamaker.util.FXEventBus;
import com.github.esslerc.pdfamaker.util.SettingsSavedEvent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

public class SettingsService {

    private static final String CONFIG_FILE_NAME = "app.properties";

    private final File propertiesFile;
    private final Properties properties;

    public SettingsService() {
        this(getDefaultPropertiesPath());
    }

    public SettingsService(Path baseDir) {
        properties = new Properties();
        File propertiesDir = baseDir.toFile();
        if (!propertiesDir.exists()) {
            propertiesDir.mkdir();
        }
        this.propertiesFile = new File(propertiesDir, CONFIG_FILE_NAME);
        reloadProperties();
    }

    private static Path getDefaultPropertiesPath() {
        String userHome = System.getProperty("user.home");
        if (isWindows()) {
            return Paths.get(System.getenv("APPDATA"), "pdfa_maker");
        } else {
            return Paths.get(userHome, ".pdfa_maker");
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public synchronized void reloadProperties() {
        if (propertiesFile.exists()) {
            try (InputStream input = new FileInputStream(propertiesFile)) {
                properties.clear();
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void saveProperties() {
        try (OutputStream output = new FileOutputStream(propertiesFile)) {
            properties.store(output, "Application Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized SettingsData getSettingsData() {
        PDFAStandard pdfaStandard = null;
        String outputDirectory = null;
        Locale locale = null;

        for (Field field : SettingsData.class.getDeclaredFields()) {
            String key = field.getName();
            if (properties.containsKey(key)) {
                field.setAccessible(true);
                String value = properties.getProperty(key);

                if (field.getType().equals(PDFAStandard.class)) {
                    pdfaStandard = PDFAStandard.getEnumForValue(value);
                } else if (field.getType().equals(Locale.class)) {
                    locale = Locale.of(value);
                } else {
                    outputDirectory = value;
                }
            }
        }
        if(pdfaStandard == null) pdfaStandard = PDFAStandard.PDFA_2b;
        if(outputDirectory == null) outputDirectory = System.getProperty("user.home");
        if(locale == null) locale = Locale.getDefault();

        return new SettingsData(outputDirectory, pdfaStandard, locale);
    }

    public synchronized void saveSettingsData(SettingsData settingsData) {
        for (Field field : SettingsData.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(settingsData);
                if (value != null) {
                    if(field.getType() == PDFAStandard.class) {
                        PDFAStandard pdfaStandard = (PDFAStandard) value;
                        properties.setProperty(field.getName(), pdfaStandard.getLabel());
                    } else if (field.getType().equals(Locale.class)) {
                        Locale locale = (Locale) value;
                        properties.setProperty(field.getName(), locale.getLanguage());
                    } else {
                        properties.setProperty(field.getName(), value.toString());
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        saveProperties();
        FXEventBus.publish(new SettingsSavedEvent(settingsData));
    }

    public void setStageAppIcon(Stage stage) {
        stage.getIcons().addAll(
                getImage("/icons/app_icons/icon.iconset/icon_16x16.png"),
                getImage("/icons/app_icons/icon.iconset/icon_32x32.png"),
                getImage("/icons/app_icons/icon.iconset/icon_48x48.png"),
                getImage("/icons/app_icons/icon.iconset/icon_64x64.png"),
                getImage("/icons/app_icons/icon.iconset/icon_128x128.png"),
                getImage("/icons/app_icons/icon.iconset/icon_256x256.png"),
                getImage("/icons/app_icons/icon.iconset/icon_512x512.png"),
                getImage("/icons/app_icons/icon.iconset/icon_1024x1024.png")
        );
    }

    private Image getImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}

