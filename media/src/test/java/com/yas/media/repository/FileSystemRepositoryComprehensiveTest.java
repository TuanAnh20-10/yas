package com.yas.media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.media.config.FilesystemConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FileSystemRepositoryComprehensiveTest {

    @Mock
    private FilesystemConfig filesystemConfig;

    private FileSystemRepository fileSystemRepository;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        fileSystemRepository = new FileSystemRepository(filesystemConfig);
        org.mockito.Mockito.when(filesystemConfig.getDirectory()).thenReturn(tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
            .sorted((p1, p2) -> p2.compareTo(p1))
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    @Test
    void persistFile_withValidFile_shouldWriteSuccessfully() throws IOException {
        String filename = "test_document.txt";
        byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        String resultPath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(resultPath);
        assertTrue(Files.exists(Paths.get(resultPath)));
        assertArrayEquals(content, Files.readAllBytes(Paths.get(resultPath)));
    }

    @Test
    void persistFile_withPathTraversal_shouldThrowIllegalArgumentException() {
        String maliciousFilename = "../../../etc/passwd";
        byte[] content = "malicious".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(maliciousFilename, content)
        );
    }

    @Test
    void persistFile_withBackslashTraversal_shouldThrowIllegalArgumentException() {
        String maliciousFilename = "..\\..\\..\\windows\\system32\\config";
        byte[] content = "malicious".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(maliciousFilename, content)
        );
    }

    @Test
    void persistFile_withForwardSlashInFilename_shouldThrowIllegalArgumentException() {
        String maliciousFilename = "subdir/file.txt";
        byte[] content = "content".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(maliciousFilename, content)
        );
    }

    @Test
    void persistFile_withAbsolutePath_shouldThrowIllegalArgumentException() {
        String absolutePath = "/etc/passwd";
        byte[] content = "malicious".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(absolutePath, content)
        );
    }

    @Test
    void persistFile_withDoubleDotsOnly_shouldThrowIllegalArgumentException() {
        String filename = "..";
        byte[] content = "content".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(filename, content)
        );
    }

    @Test
    void persistFile_withNonExistentDirectory_shouldThrowIllegalStateException() {
        org.mockito.Mockito.when(filesystemConfig.getDirectory())
            .thenReturn("/non/existent/directory");
        String filename = "test.txt";
        byte[] content = "content".getBytes();

        assertThrows(
            IllegalStateException.class,
            () -> fileSystemRepository.persistFile(filename, content)
        );
    }

    @Test
    void persistFile_withLargeFile_shouldHandleSuccessfully() throws IOException {
        String filename = "large_file.bin";
        byte[] largeContent = new byte[1024 * 1024];
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        String resultPath = fileSystemRepository.persistFile(filename, largeContent);

        assertNotNull(resultPath);
        assertTrue(Files.exists(Paths.get(resultPath)));
        assertEquals(largeContent.length, Files.size(Paths.get(resultPath)));
    }

    @Test
    void persistFile_withEmptyFile_shouldWriteSuccessfully() throws IOException {
        String filename = "empty.txt";
        byte[] content = new byte[0];

        String resultPath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(resultPath);
        assertTrue(Files.exists(Paths.get(resultPath)));
        assertEquals(0, Files.size(Paths.get(resultPath)));
    }

    @Test
    void persistFile_withSpecialCharactersInFilename_shouldWriteSuccessfully() throws IOException {
        String filename = "test-file_123.png";
        byte[] content = "png content".getBytes();

        String resultPath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(resultPath);
        assertTrue(Files.exists(Paths.get(resultPath)));
    }

    @Test
    void persistFile_withUnicodeFilename_shouldWriteSuccessfully() throws IOException {
        String filename = "测试文件.txt";
        byte[] content = "Unicode content".getBytes(StandardCharsets.UTF_8);

        String resultPath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(resultPath);
        assertTrue(Files.exists(Paths.get(resultPath)));
    }

    @Test
    void getFile_withValidPath_shouldReturnInputStream() throws IOException {
        String filename = "readable.txt";
        byte[] content = "Read this file".getBytes(StandardCharsets.UTF_8);
        String savedPath = fileSystemRepository.persistFile(filename, content);

        InputStream resultInputStream = fileSystemRepository.getFile(savedPath);

        assertNotNull(resultInputStream);
        byte[] readContent = resultInputStream.readAllBytes();
        assertArrayEquals(content, readContent);
    }

    @Test
    void getFile_withNonExistentFile_shouldThrowIllegalStateException() {
        String nonExistentPath = "/non/existent/path/file.txt";

        assertThrows(
            IllegalStateException.class,
            () -> fileSystemRepository.getFile(nonExistentPath)
        );
    }

    @Test
    void getFile_withNullPath_shouldThrowException() {
        assertThrows(
            Exception.class,
            () -> fileSystemRepository.getFile(null)
        );
    }

    @Test
    void persistFile_withFileNameContainingDotDot_shouldThrowIllegalArgumentException() {
        String filename = "file..name.txt";
        byte[] content = "content".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(filename, content)
        );
    }

    @Test
    void persistFile_multipleFilesWithDifferentNames_shouldAllBeSavedSeparately()
        throws IOException {
        String[] filenames = {"file1.txt", "file2.txt", "file3.txt"};
        byte[][] contents = {
            "content1".getBytes(),
            "content2".getBytes(),
            "content3".getBytes()
        };

        String[] paths = new String[3];
        for (int i = 0; i < filenames.length; i++) {
            paths[i] = fileSystemRepository.persistFile(filenames[i], contents[i]);
        }

        for (int i = 0; i < filenames.length; i++) {
            assertTrue(Files.exists(Paths.get(paths[i])));
            assertArrayEquals(contents[i], Files.readAllBytes(Paths.get(paths[i])));
        }
    }

    @Test
    void persistFile_overwriteExistingFile_shouldUpdateContent() throws IOException {
        String filename = "overwrite_test.txt";
        byte[] originalContent = "original".getBytes();
        byte[] newContent = "updated content".getBytes();

        String path1 = fileSystemRepository.persistFile(filename, originalContent);
        String path2 = fileSystemRepository.persistFile(filename, newContent);

        assertEquals(path1, path2);
        assertTrue(Files.exists(Paths.get(path2)));
        assertArrayEquals(newContent, Files.readAllBytes(Paths.get(path2)));
    }

    @Test
    void persistFile_withBinaryContent_shouldHandleSuccessfully() throws IOException {
        String filename = "binary.dat";
        byte[] binaryContent = {
            (byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04
        };

        String resultPath = fileSystemRepository.persistFile(filename, binaryContent);

        assertNotNull(resultPath);
        assertTrue(Files.exists(Paths.get(resultPath)));
        assertArrayEquals(binaryContent, Files.readAllBytes(Paths.get(resultPath)));
    }

    @Test
    void persistFile_withNullContent_shouldHandleSuccessfully() throws IOException {
        String filename = "null_file.txt";
        byte[] content = null;

        assertThrows(
            Exception.class,
            () -> fileSystemRepository.persistFile(filename, content)
        );
    }

    @Test
    void persistFile_pathStartsWithBaseDirectory_shouldSucceed() throws IOException {
        String filename = "valid_file.txt";
        byte[] content = "valid content".getBytes();

        String resultPath = fileSystemRepository.persistFile(filename, content);

        assertNotNull(resultPath);
        Path resultPathObj = Paths.get(resultPath).toAbsolutePath();
        Path baseDirObj = Paths.get(tempDir.toString()).toAbsolutePath();
        assertTrue(resultPathObj.startsWith(baseDirObj));
    }
}