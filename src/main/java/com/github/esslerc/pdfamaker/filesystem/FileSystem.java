package com.github.esslerc.pdfamaker.filesystem;

public interface FileSystem {
    Boolean exists(String path);
    Boolean mkdirs(String path);
}
