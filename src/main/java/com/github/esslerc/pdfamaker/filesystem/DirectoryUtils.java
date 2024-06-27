package com.github.esslerc.pdfamaker.filesystem;

public class DirectoryUtils {

    private static FileSystem FILESYSTEM = new DefaultFileSystem();

    public static void setFileSystem(FileSystem fileSystem) {
        FILESYSTEM = fileSystem;
    }


    public static void ensureDirExists(String path) {
        if (!FILESYSTEM.exists(path)) {
            FILESYSTEM.mkdirs(path);
        }
    }
}
