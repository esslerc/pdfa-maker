package com.github.esslerc.pdfamaker.util;

import java.io.File;

public class DirectoryUtils {

    public static boolean ensureDirExists(String path) {
        File file = new File(path);
        return !file.exists() && file.mkdirs();
    }
}
