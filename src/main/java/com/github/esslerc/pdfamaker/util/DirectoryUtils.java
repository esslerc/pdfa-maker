package com.github.esslerc.pdfamaker.util;

import java.io.File;

public class DirectoryUtils {

    public static boolean ensureDirExists(String path) {
        File file = new File(path);

        if(!file.exists()) {
            return file.mkdirs();
        } else {
            return file.isDirectory() && file.canWrite();
        }
    }
}
