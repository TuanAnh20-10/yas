package com.yas.media.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.media.config.FilesystemConfig;
import com.yas.media.repository.FileSystemRepository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("File System Integration Tests")
class FileSystemIntegrationTest {

    @Mock
    private FilesystemConfig filesystemConfig;

    private FileSystemRepository fileSystemRepository;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Workflow: Save and retrieve file with correct content")
    void workflow_saveAndRetrieveFile() throws IOException {
        String filename = "document.txt";
        String content = "This is the document content";
        byte[] fileContent = content.getBytes(StandardCharsets.UTF_8);

        String savedPath = fileSystemRepository.persistFile(filename, fileContent);
        assertNotNull(savedPath);
        assertTrue(Files.exists(Paths.get(savedPath)));

        InputStream inputStream = fileSystemRepository.getFile(savedPath);
        byte[] retrievedContent = inputStream.readAllBytes();

        assertEquals(content, new String(retrievedContent, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Workflow: Multiple files can coexist independently")
    void workflow_multipleFilesCoexist() throws IOException {
        String[] filenames = {"file1.txt", "file2.txt", "file3.txt"};
        String[] contents = {
            "Content of file 1",
            "Content of file 2",
            "Content of file 3"
        };

        String[] savedPaths = new String[3];
        for (int i = 0; i < filenames.length; i++) {
            savedPaths[i] = fileSystemRepository.persistFile(
                filenames[i],
                contents[i].getBytes(StandardCharsets.UTF_8)
            );
        }

        for (int i = 0; i < filenames.length; i++) {
            assertTrue(Files.exists(Paths.get(savedPaths[i])));
            InputStream inputStream = fileSystemRepository.getFile(savedPaths[i]);
            byte[] retrievedContent = inputStream.readAllBytes();
            assertEquals(
                contents[i],
                new String(retrievedContent, StandardCharsets.UTF_8)
            );
        }

        assertEquals(3, tempDir.toFile().listFiles().length);
    }

    @Test
    @DisplayName("Security: Path traversal attempt is blocked")
    void security_pathTraversalBlocked() {
        String maliciousPath = "../../../etc/passwd";
        byte[] content = "malicious".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(maliciousPath, content)
        );

        assertTrue(Files.exists(tempDir));
    }

    @Test
    @DisplayName("Security: Absolute path injection is blocked")
    void security_absolutePathBlocked() {
        String absolutePath = "/etc/passwd";
        byte[] content = "malicious".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(absolutePath, content)
        );
    }

    @Test
    @DisplayName("Performance: Large file operations complete successfully")
    void performance_largeFileHandling() throws IOException {
        String filename = "large_file.bin";
        byte[] largeContent = new byte[5 * 1024 * 1024]; // 5MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) ((i * 7 + 3) % 256);
        }

        long startTime = System.currentTimeMillis();
        String savedPath = fileSystemRepository.persistFile(filename, largeContent);
        long saveTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        InputStream inputStream = fileSystemRepository.getFile(savedPath);
        byte[] retrievedContent = inputStream.readAllBytes();
        long retrieveTime = System.currentTimeMillis() - startTime;

        assertEquals(largeContent.length, retrievedContent.length);
        assertArrayEquals(largeContent, retrievedContent);
        assertTrue(saveTime < 5000, "Save operation took too long: " + saveTime + "ms");
        assertTrue(retrieveTime < 5000, "Retrieve operation took too long: " + retrieveTime + "ms");
    }

    @Test
    @DisplayName("Data integrity: File overwrite preserves file integrity")
    void dataIntegrity_fileOverwrite() throws IOException {
        String filename = "version_controlled.txt";
        String version1 = "Version 1.0 - Initial release";
        String version2 = "Version 2.0 - Major update with new features";

        String path1 = fileSystemRepository.persistFile(
            filename,
            version1.getBytes(StandardCharsets.UTF_8)
        );

        String path2 = fileSystemRepository.persistFile(
            filename,
            version2.getBytes(StandardCharsets.UTF_8)
        );

        assertEquals(path1, path2);
        byte[] finalContent = Files.readAllBytes(Paths.get(path2));
        assertEquals(version2, new String(finalContent, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Workflow: Binary file handling preserves exact bytes")
    void workflow_binaryFileHandling() throws IOException {
        String filename = "binary_data.bin";
        byte[] binaryContent = {
            (byte) 0x00, (byte) 0xFF, (byte) 0x80, (byte) 0x7F,
            (byte) 0xAA, (byte) 0x55, (byte) 0x12, (byte) 0x34,
            (byte) 0x56, (byte) 0x78, (byte) 0x9A, (byte) 0xBC,
            (byte) 0xDE, (byte) 0xF0, (byte) 0x11, (byte) 0x22
        };

        String savedPath = fileSystemRepository.persistFile(filename, binaryContent);
        InputStream inputStream = fileSystemRepository.getFile(savedPath);
        byte[] retrievedContent = inputStream.readAllBytes();

        assertArrayEquals(binaryContent, retrievedContent);
    }

    @Test
    @DisplayName("Workflow: Empty file handling")
    void workflow_emptyFileHandling() throws IOException {
        String filename = "empty.txt";
        byte[] emptyContent = new byte[0];

        String savedPath = fileSystemRepository.persistFile(filename, emptyContent);
        InputStream inputStream = fileSystemRepository.getFile(savedPath);
        byte[] retrievedContent = inputStream.readAllBytes();

        assertEquals(0, retrievedContent.length);
        assertTrue(Files.exists(Paths.get(savedPath)));
    }

    @Test
    @DisplayName("Workflow: File with special characters in name")
    void workflow_specialCharacterFilenames() throws IOException {
        String filename = "my-file_v1.2-test.txt";
        byte[] content = "Special characters in filename".getBytes();

        String savedPath = fileSystemRepository.persistFile(filename, content);
        InputStream inputStream = fileSystemRepository.getFile(savedPath);
        byte[] retrievedContent = inputStream.readAllBytes();

        assertEquals(new String(content), new String(retrievedContent));
    }

    @Test
    @DisplayName("Workflow: Sequential save operations on different files")
    void workflow_sequentialOperations() throws IOException {
        String[] filenames = {"first.txt", "second.txt", "third.txt"};

        for (int i = 0; i < filenames.length; i++) {
            String content = "Content " + (i + 1);
            fileSystemRepository.persistFile(
                filenames[i],
                content.getBytes(StandardCharsets.UTF_8)
            );
        }

        for (String filename : filenames) {
            Path filePath = tempDir.resolve(filename);
            assertTrue(Files.exists(filePath), "File should exist: " + filename);
        }

        assertEquals(filenames.length, tempDir.toFile().listFiles().length);
    }

    @Test
    @DisplayName("Error handling: Invalid path with multiple traversal attempts")
    void errorHandling_complexTraversalAttempt() {
        String complexMaliciousPath = "../../../../../../etc/passwd";
        byte[] content = "malicious".getBytes();

        assertThrows(
            IllegalArgumentException.class,
            () -> fileSystemRepository.persistFile(complexMaliciousPath, content)
        );
    }

    @Test
    @DisplayName("Error handling: Non-existent file retrieval")
    void errorHandling_nonExistentFileRetrieval() {
        String nonExistentPath = tempDir.resolve("ghost_file.txt").toString();

        assertThrows(
            IllegalStateException.class,
            () -> fileSystemRepository.getFile(nonExistentPath)
        );
    }

    @Test
    @DisplayName("File path validation: Correct base directory validation")
    void filePathValidation_correctBaseDirectory() throws IOException {
        String filename = "valid_file.txt";
        byte[] content = "valid content".getBytes();

        String savedPath = fileSystemRepository.persistFile(filename, content);

        Path savedPathObj = Paths.get(savedPath).toAbsolutePath();
        Path baseDirObj = tempDir.toAbsolutePath();
        assertTrue(savedPathObj.startsWith(baseDirObj));
    }

    @Test
    @DisplayName("Workflow: Realistic image file save and retrieve")
    void workflow_imageFileSaveRetrieve() throws IOException {
        String filename = "sample_image.jpg";
        byte[] jpegHeader = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01
        };

        String savedPath = fileSystemRepository.persistFile(filename, jpegHeader);

        InputStream inputStream = fileSystemRepository.getFile(savedPath);
        byte[] retrievedHeader = inputStream.readAllBytes();

        assertArrayEquals(jpegHeader, retrievedHeader);
    }
}
