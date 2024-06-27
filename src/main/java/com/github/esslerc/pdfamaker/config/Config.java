package com.github.esslerc.pdfamaker.config;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    private static Properties props = new Properties();

    static {
        try {
            props.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}
