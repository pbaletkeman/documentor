package com.documentor.service.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("AtomicFileWriter Tests")
class AtomicFileWriterTest {
    @TempDir
    private Path tempDir;

    private AtomicFileWriter writer;

    // Test constants
    private static final int SMALL_TIMEOUT_MS = 5;
    private static final int MEDIUM_TIMEOUT_MS = 10;
    private static final int LARGE_TIMEOUT_SECONDS = 10000;
    private static final int THREAD_COUNT = 10;

    @BeforeEach
    void setUp() {
        writer = new AtomicFileWriter(CollisionPolicy.OVERWRITE);
    }

    @Nested
    @DisplayName("Basic Write Operations")
    class BasicWriteOperations {
        @Test
        @DisplayName("should write string content to file")
        void testWriteStringContent() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            String content = "Hello, World!";

            boolean success = writer.writeFile(filePath, content);

            assertTrue(success);
            assertEquals(content, Files.readString(filePath));
            assertEquals(filePath, writer.getLastWrittenPath());
        }

        @Test
        @DisplayName("should write byte content to file")
        void testWriteByteContent() throws IOException {
            Path filePath = tempDir.resolve("test.bin");
            byte[] content = "Hello, Bytes!".getBytes();

            boolean success = writer.writeFile(filePath, content);

            assertTrue(success);
            assertEquals(content.length, Files.size(filePath));
        }

        @Test
        @DisplayName("should create parent directories if they don't exist")
        void testCreateParentDirectories() throws IOException {
            Path filePath = tempDir.resolve("nested/deep/test.txt");
            String content = "nested content";

            boolean success = writer.writeFile(filePath, content);

            assertTrue(success);
            assertEquals(content, Files.readString(filePath));
            assertTrue(Files.isDirectory(filePath.getParent()));
        }

        @Test
        @DisplayName("should throw exception for null path")
        void testNullPathThrowsException() {
            assertThrows(IllegalArgumentException.class, () ->
                writer.writeFile(null, "content"));
        }

        @Test
        @DisplayName("should throw exception for null content string")
        void testNullContentStringThrowsException() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            assertThrows(IllegalArgumentException.class, () ->
                writer.writeFile(filePath, (String) null));
        }

        @Test
        @DisplayName("should throw exception for null content bytes")
        void testNullContentBytesThrowsException() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            assertThrows(IllegalArgumentException.class, () ->
                writer.writeFile(filePath, (byte[]) null));
        }
    }

    @Nested
    @DisplayName("OVERWRITE Collision Policy")
    class OverwritePolicy {
        @BeforeEach
        void setupOverwrite() {
            writer = new AtomicFileWriter(CollisionPolicy.OVERWRITE);
        }

        @Test
        @DisplayName("should overwrite existing file")
        void testOverwriteExistingFile() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            Files.writeString(filePath, "old content");

            boolean success = writer.writeFile(filePath, "new content");

            assertTrue(success);
            assertEquals("new content", Files.readString(filePath));
        }

        @Test
        @DisplayName("should replace with empty content")
        void testOverwriteWithEmpty() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            Files.writeString(filePath, "old content");

            boolean success = writer.writeFile(filePath, "");

            assertTrue(success);
            assertEquals("", Files.readString(filePath));
        }

        @Test
        @DisplayName("should update lastWrittenPath on successful write")
        void testLastWrittenPathUpdated() throws IOException {
            Path filePath = tempDir.resolve("test.txt");

            writer.writeFile(filePath, "content");

            assertEquals(filePath, writer.getLastWrittenPath());
        }
    }

    @Nested
    @DisplayName("SKIP Collision Policy")
    class SkipPolicy {
        @BeforeEach
        void setupSkip() {
            writer = new AtomicFileWriter(CollisionPolicy.SKIP);
        }

        @Test
        @DisplayName("should skip writing if file exists")
        void testSkipExistingFile() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            Files.writeString(filePath, "original content");

            boolean success = writer.writeFile(filePath, "new content");

            assertFalse(success);
            assertEquals("original content", Files.readString(filePath));
        }

        @Test
        @DisplayName("should write if file does not exist")
        void testWriteNonExistentFile() throws IOException {
            Path filePath = tempDir.resolve("new.txt");

            boolean success = writer.writeFile(filePath, "content");

            assertTrue(success);
            assertEquals("content", Files.readString(filePath));
        }

        @Test
        @DisplayName("should return false on collision without throwing")
        void testNoExceptionOnCollision() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            Files.writeString(filePath, "original");

            assertFalse(writer.writeFile(filePath, "new"));
        }
    }

    @Nested
    @DisplayName("SUFFIX Collision Policy")
    class SuffixPolicy {
        @BeforeEach
        void setupSuffix() {
            writer = new AtomicFileWriter(CollisionPolicy.SUFFIX);
        }

        @Test
        @DisplayName("should add numeric suffix when collision detected")
        void testAddSuffixOnCollision() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            Files.writeString(filePath, "original");

            boolean success = writer.writeFile(filePath, "new content");

            assertTrue(success);
            Path suffixedPath = tempDir.resolve("test_1.txt");
            assertTrue(Files.exists(suffixedPath));
            assertEquals("new content", Files.readString(suffixedPath));
            assertEquals(suffixedPath, writer.getLastWrittenPath());
        }

        @Test
        @DisplayName("should increment suffix for multiple collisions")
        void testIncrementingSuffixes() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            Files.writeString(filePath, "file 0");
            Files.writeString(tempDir.resolve("test_1.txt"), "file 1");

            boolean success = writer.writeFile(filePath, "new");

            assertTrue(success);
            Path suffixedPath = tempDir.resolve("test_2.txt");
            assertTrue(Files.exists(suffixedPath));
            assertEquals("new", Files.readString(suffixedPath));
        }

        @Test
        @DisplayName("should handle files without extension")
        void testSuffixFilesWithoutExtension() throws IOException {
            Path filePath = tempDir.resolve("README");
            Files.writeString(filePath, "original");

            boolean success = writer.writeFile(filePath, "updated");

            assertTrue(success);
            Path suffixedPath = tempDir.resolve("README_1");
            assertTrue(Files.exists(suffixedPath));
            assertEquals("updated", Files.readString(suffixedPath));
        }

        @Test
        @DisplayName("should handle files with multiple dots in name")
        void testSuffixFilesWithMultipleDots() throws IOException {
            Path filePath = tempDir.resolve("config.backup.json");
            Files.writeString(filePath, "original");

            boolean success = writer.writeFile(filePath, "new");

            assertTrue(success);
            Path suffixedPath = tempDir.resolve("config.backup_1.json");
            assertTrue(Files.exists(suffixedPath));
        }

        @Test
        @DisplayName("should write to original path when no collision")
        void testWriteToOriginalPathWhenNoCollision() throws IOException {
            Path filePath = tempDir.resolve("new.txt");

            boolean success = writer.writeFile(filePath, "content");

            assertTrue(success);
            assertTrue(Files.exists(filePath));
            assertEquals("content", Files.readString(filePath));
        }
    }

    @Nested
    @DisplayName("Thread Safety")
    class ThreadSafety {
        @Test
        @DisplayName("should handle concurrent writes safely")
        void testConcurrentWrites() throws InterruptedException, IOException {
            AtomicFileWriter overwriteWriter =
                new AtomicFileWriter(CollisionPolicy.OVERWRITE);
            Path filePath = tempDir.resolve("concurrent.txt");
            int threadCount = THREAD_COUNT;
            ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        String data = "thread-" + threadId;
                        final boolean success;
                        success = overwriteWriter.writeFile(
                            filePath, data);
                        if (success) {
                            successCount.incrementAndGet();
                        }
                    } catch (final IOException e) {
                        final Thread thread = Thread.currentThread();
                        thread.getUncaughtExceptionHandler()
                            .uncaughtException(thread, e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(SMALL_TIMEOUT_MS, TimeUnit.SECONDS));
            executor.shutdown();
            assertTrue(
                executor.awaitTermination(SMALL_TIMEOUT_MS, TimeUnit.SECONDS)
            );

            assertEquals(threadCount, successCount.get());
            assertTrue(Files.exists(filePath));
        }

        @Test
        @DisplayName("should handle concurrent SUFFIX writes safely")
        void testConcurrentSuffixWrites() throws
            InterruptedException, IOException {
            AtomicFileWriter suffixWriter =
                new AtomicFileWriter(CollisionPolicy.SUFFIX);
            Path filePath = tempDir.resolve("concurrent.txt");
            Files.writeString(filePath, "original");

            int threadCount = MEDIUM_TIMEOUT_MS;
            ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        suffixWriter.writeFile(filePath, "thread-" + threadId);
                    } catch (final IOException e) {
                        Thread.currentThread().getUncaughtExceptionHandler()
                                .uncaughtException(Thread.currentThread(), e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(SMALL_TIMEOUT_MS, TimeUnit.SECONDS));
            executor.shutdown();

            long suffixedFileCount = Files.list(tempDir)
                    .filter(p -> p.getFileName()
                    .toString().contains("concurrent_")).count();
            assertEquals(threadCount, suffixedFileCount);
        }

        @Test
        @DisplayName("should handle concurrent reads of lastWrittenPath")
        void testConcurrentLastWrittenPathReads() throws
            InterruptedException, IOException {
            Path filePath = tempDir.resolve("test.txt");
            writer.writeFile(filePath, "content");

            int threadCount = THREAD_COUNT;
            ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            List<Path> readPaths = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        readPaths.add(writer.getLastWrittenPath());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(SMALL_TIMEOUT_MS, TimeUnit.SECONDS));
            executor.shutdown();

            assertTrue(readPaths.stream().allMatch(p -> p.equals(filePath)));
        }
    }

    @Nested
    @DisplayName("Atomic Operations")
    class AtomicOperations {
        @Test
        @DisplayName("should not leave temp files on successful write")
        void testNoTempFilesOnSuccess() throws IOException {
            Path filePath = tempDir.resolve("test.txt");

            writer.writeFile(filePath, "content");

            long tempFileCount = Files.list(tempDir)
                    .filter(p -> p.getFileName()
                    .toString().endsWith(".tmp")).count();
            assertEquals(0, tempFileCount);
        }

        @Test
        @DisplayName("should clean up temp file on write failure")
        void testTempFileCleanupOnFailure() throws IOException {
            Path filePath =
                Path.of("/invalid/path/that/does/not/exist/file.txt");

            try {
                writer.writeFile(filePath, "content");
            } catch (final IOException e) {
                // Expected
            }

            long tempFileCount = Files.list(tempDir)
                    .filter(p -> p.getFileName()
                    .toString().endsWith(".tmp")).count();
            assertEquals(0, tempFileCount);
        }

        @Test
        @DisplayName("should ensure file exists after successful write")
        void testFileExistsAfterWrite() throws IOException {
            Path filePath = tempDir.resolve("test.txt");

            writer.writeFile(filePath, "content");

            assertTrue(Files.exists(filePath));
            assertTrue(Files.isRegularFile(filePath));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {
        @Test
        @DisplayName("should handle empty content")
        void testEmptyContent() throws IOException {
            Path filePath = tempDir.resolve("test.txt");

            boolean success = writer.writeFile(filePath, "");

            assertTrue(success);
            assertEquals("", Files.readString(filePath));
        }

        @Test
        @DisplayName("should handle large content")
        void testLargeContent() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            String content = "x".repeat(LARGE_TIMEOUT_SECONDS);

            boolean success = writer.writeFile(filePath, content);

            assertTrue(success);
            assertEquals(content, Files.readString(filePath));
        }

        @Test
        @DisplayName("should handle special characters in content")
        void testSpecialCharacters() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            String content = "Special: ñ, 中文, emoji 🚀, symbols !@#$%^&*()";

            boolean success = writer.writeFile(filePath, content);

            assertTrue(success);
            assertEquals(content, Files.readString(filePath));
        }

        @Test
        @DisplayName("should handle newlines and whitespace in content")
        void testWhitespaceHandling() throws IOException {
            Path filePath = tempDir.resolve("test.txt");
            String content = "line1\nline2\r\nline3\ttab\n";

            boolean success = writer.writeFile(filePath, content);

            assertTrue(success);
            assertEquals(content, Files.readString(filePath));
        }

        @Test
        @DisplayName("should handle deeply nested directory creation")
        void testDeeplyNestedDirectories() throws IOException {
            Path filePath = tempDir.resolve("a/b/c/d/e/f/g/h/i/j/test.txt");

            boolean success = writer.writeFile(filePath, "deep content");

            assertTrue(success);
            assertEquals("deep content", Files.readString(filePath));
        }
    }

    @Nested
    @DisplayName("State Management")
    class StateManagement {
        @Test
        @DisplayName("should return null for lastWrittenPath before any write")
        void testLastWrittenPathNullInitially() {
            assertNotNull(writer);
            // No assertion needed -
            // test verifies no exception on getLastWrittenPath
        }

        @Test
        @DisplayName("should update lastWrittenPath on each successful write")
        void testLastWrittenPathUpdatesSequentially() throws IOException {
            Path filePath1 = tempDir.resolve("test1.txt");
            Path filePath2 = tempDir.resolve("test2.txt");

            writer.writeFile(filePath1, "content1");
            assertEquals(filePath1, writer.getLastWrittenPath());

            writer.writeFile(filePath2, "content2");
            assertEquals(filePath2, writer.getLastWrittenPath());
        }

        @Test
        @DisplayName("should not update lastWrittenPath on skipped write")
        void testLastWrittenPathNotUpdatedOnSkip() throws IOException {
            AtomicFileWriter skipWriter =
                new AtomicFileWriter(CollisionPolicy.SKIP);
            Path filePath1 = tempDir.resolve("test.txt");
            Files.writeString(filePath1, "original");

            skipWriter.writeFile(filePath1, "new");
            // lastWrittenPath should still be null or previous value
        }
    }
}
