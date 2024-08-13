package com.github.esslerc.pdfamaker.config;

import com.github.esslerc.pdfamaker.service.PDFAStandard;

import java.io.*;
import java.util.Properties;
import java.lang.reflect.Field;

public class ConfigService {

    private static final String CONFIG_FILE_NAME = "app.properties";
    private final Properties properties;
    private final File configFile;
    private final AppConfig appConfig;

    public ConfigService() {
        properties = new Properties();
        appConfig = new AppConfig();
        String configDirPath = getConfigDirPath();
        File configDir = new File(configDirPath);
        if (!configDir.exists()) {
            configDir.mkdir();
        }

        configFile = new File(configDir, CONFIG_FILE_NAME);
        loadProperties();
        loadPropertiesIntoBean();
    }

    protected String getConfigDirPath() {
        String userHome = System.getProperty("user.home");
        if (isWindows()) {
            return System.getenv("APPDATA") + File.separator + "pdfa_maker";
        } else {
            return userHome + File.separator + ".pdfa_maker";
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    protected void loadProperties() {
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveProperties() {
        try (OutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, "Application Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadPropertiesIntoBean() {
        for (Field field : AppConfig.class.getDeclaredFields()) {
            String key = field.getName();
            if (properties.containsKey(key)) {
                field.setAccessible(true);
                try {
                    String value = properties.getProperty(key);
                    if (field.getType() == PDFAStandard.class) {
                        field.set(appConfig, PDFAStandard.getEnumForValue(value));
                    } else {
                        field.set(appConfig, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void savePropertiesFromBean() {
        for (Field field : AppConfig.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(appConfig);
                if (value != null) {
                    if(field.getType() == PDFAStandard.class) {
                        PDFAStandard pdfaStandard = (PDFAStandard) value;
                        properties.setProperty(field.getName(), pdfaStandard.getLabel());
                    } else {
                        properties.setProperty(field.getName(), value.toString());
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        saveProperties();
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
