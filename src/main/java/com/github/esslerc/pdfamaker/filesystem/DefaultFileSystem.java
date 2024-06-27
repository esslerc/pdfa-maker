package com.github.esslerc.pdfamaker.filesystem;

import java.io.File;

public class DefaultFileSystem implements FileSystem {
    @Override
    public Boolean exists(String path) {
        return new File(path).exists();
    }

    @Override
    public Boolean mkdirs(String path) {
        return new File(path).mkdirs();
    }
}
