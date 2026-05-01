package com.yas.media.repository;

import com.yas.media.config.FilesystemConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FileSystemRepositoryTest {
    @Mock
    private FilesystemConfig filesystemConfig;
    @InjectMocks
    private FileSystemRepository fileSystemRepository;

    private final String testDir = "src/test/resources/test-directory";

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        Path absTestDir = Paths.get(testDir).toAbsolutePath();
        Files.createDirectories(absTestDir);
        when(filesystemConfig.getDirectory()).thenReturn(absTestDir.toString());
    }

    @Test
    void persistFile_shouldSaveFile() throws IOException {
        String filename = "test-file.txt";
        byte[] content = "hello".getBytes();
        String path = fileSystemRepository.persistFile(filename, content);
        assertTrue(Files.exists(Paths.get(path)));
        Files.deleteIfExists(Paths.get(path));
    }

    @Test
    void persistFile_invalidFilename_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile("../evil.txt", new byte[]{}));
    }

    @Test
    void persistFile_invalidFilePath_shouldThrow() {
        String absPath = java.nio.file.Paths.get("/tmp/evil.txt").toAbsolutePath().toString();
        assertThrows(IllegalArgumentException.class, () -> fileSystemRepository.persistFile(absPath, new byte[]{}));
    }

    @Test
    void persistFile_directoryNotExist_shouldThrow() {
        when(filesystemConfig.getDirectory()).thenReturn("not-exist-dir");
        assertThrows(IllegalStateException.class, () -> fileSystemRepository.persistFile("file.txt", new byte[]{}));
    }

    @Test
    void getFile_fileNotExist_shouldThrow() {
        assertThrows(IllegalStateException.class, () -> fileSystemRepository.getFile("not-exist-file.txt"));
    }

    @Test
    void persistFile_directoryNoPermission_shouldThrow() {
        class TestFileSystemRepository extends FileSystemRepository {
            TestFileSystemRepository(FilesystemConfig config) { super(config); }
            @Override
            protected void checkPermissions(File directory) {
                throw new IllegalStateException("Directory not accessible");
            }
        }
        FileSystemRepository repo = new TestFileSystemRepository(filesystemConfig);
        when(filesystemConfig.getDirectory()).thenReturn(testDir);
        assertThrows(IllegalStateException.class, () -> repo.persistFile("file.txt", new byte[]{}));
    }

    @Test
    void getFile_shouldReturnInputStream() throws IOException {
        String filename = "test-file2.txt";
        byte[] content = "abc".getBytes();
        String path = fileSystemRepository.persistFile(filename, content);
        assertNotNull(fileSystemRepository.getFile(path));
        Files.deleteIfExists(Paths.get(path));
    }
}
