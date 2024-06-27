package com.github.esslerc.pdfamaker.filesystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DirectoryUtilsTest {

    private FileSystem fileSystemMock;

    @BeforeEach
    public void setUp() {
        fileSystemMock = mock(FileSystem.class);
        DirectoryUtils.setFileSystem(fileSystemMock);
    }

    @AfterEach
    public void tearDown() {
        // Reset to default FileSystem after each test
        DirectoryUtils.setFileSystem(new DefaultFileSystem());
    }

    @Test
    public void testEnsureDirExists_DirectoryAlreadyExists() {
        String path = "existing_directory";
        when(fileSystemMock.exists(path)).thenReturn(true);

        DirectoryUtils.ensureDirExists(path);

        verify(fileSystemMock, never()).mkdirs(path);
    }

    @Test
    public void testEnsureDirExists_DirectoryDoesNotExist() {
        String path = "new_directory";
        when(fileSystemMock.exists(path)).thenReturn(false);
        when(fileSystemMock.mkdirs(path)).thenReturn(true);

        DirectoryUtils.ensureDirExists(path);

        verify(fileSystemMock, times(1)).mkdirs(path);
    }

    @Test
    public void testEnsureDirExists_DirectoryCreationFails() {
        String path = "failing_directory";
        when(fileSystemMock.exists(path)).thenReturn(false);
        when(fileSystemMock.mkdirs(path)).thenReturn(false);

        DirectoryUtils.ensureDirExists(path);

        verify(fileSystemMock, times(1)).mkdirs(path);
    }
}