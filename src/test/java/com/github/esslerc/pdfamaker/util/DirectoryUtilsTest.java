package com.github.esslerc.pdfamaker.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectoryUtilsTest {

    @Test
    public void testExistingDirectory() throws Exception {

        Path testDir = Files.createTempDirectory("pdfa-maker-test");
        try {
            assertTrue(DirectoryUtils.ensureDirExists(testDir.toAbsolutePath().toString()));
        } finally {
            Files.deleteIfExists(testDir);
        }
    }


    @Test
    public void testCreationOfDirectory() throws IOException {

        String tempDir = System.getProperty("java.io.tmpdir");
        Path tempTestPath = Paths.get(tempDir, "pdfa-maker-test").toAbsolutePath();

        try {
            assertTrue(DirectoryUtils.ensureDirExists(tempTestPath.toString()));
            assertTrue(tempTestPath.toFile().exists());
            assertTrue(tempTestPath.toFile().isDirectory());
        } finally {
            Files.deleteIfExists(tempTestPath);
        }
    }


    @Test
    public void testDirectoryIsNotWritable() throws IOException {
        Path tmpDir = Files.createTempDirectory("pdfa-maker-test");

        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("r-xr-xr-x");
            Files.setPosixFilePermissions(tmpDir, permissions);

            assertFalse(DirectoryUtils.ensureDirExists(tmpDir.toAbsolutePath().toString()));

        } finally {
            Set<PosixFilePermission> writablePermissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(tmpDir, writablePermissions);
            Files.deleteIfExists(tmpDir);
        }
    }
}