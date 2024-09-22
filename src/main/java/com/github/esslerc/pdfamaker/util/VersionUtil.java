package com.github.esslerc.pdfamaker.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionUtil {

    public static String getVersion() {
        try (InputStream inputStream = VersionUtil.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            if (inputStream != null) {
                Manifest manifest = new Manifest(inputStream);
                Attributes attributes = manifest.getMainAttributes();

                String implementationVersion =  attributes.getValue("Implementation-Version");

                return Objects.requireNonNullElse(implementationVersion, "n.a.");
            } else {
                return "Version information not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading version information";
        }
    }

}
