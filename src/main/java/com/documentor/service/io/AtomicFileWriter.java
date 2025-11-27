package com.documentor.service.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 🔒 AtomicFileWriter - Thread-safe file writing with collision handling
 *
 * Provides atomic file write operations with configurable collision policies.
 * Ensures data integrity through write-to-temp-then-move pattern and thread
 * synchronization via ReentrantReadWriteLock for concurrent access.
 *
 * Usage:
 * <pre>{@code
 * AtomicFileWriter writer = new AtomicFileWriter(CollisionPolicy.SUFFIX);
 * boolean success = writer.writeFile(path, "content");
 * Path actualPath = writer.getLastWrittenPath();
 * }</pre>
 */
public class AtomicFileWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            AtomicFileWriter.class);
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private static final int MAX_SUFFIX_ATTEMPTS = 1000;

    private final CollisionPolicy policy;
    private final ReentrantReadWriteLock lock;
    private Path lastWrittenPath;

    /**
     * Creates an AtomicFileWriter with the specified collision policy.
     *
     * @param policy the collision policy to use (e.g., OVERWRITE, SKIP, SUFFIX)
     */
    public AtomicFileWriter(final CollisionPolicy policy) {
        this.policy = policy;
        this.lock = new ReentrantReadWriteLock();
        this.lastWrittenPath = null;
    }

    /**
     * Atomically writes content to a file using the configured collision
     * policy.
     *
     * @param targetPath the target file path
     * @param content the content to write
     * @return true if write succeeded, false if skipped due to collision
     *         or error
     * @throws IOException if an I/O error occurs during write
     */
    public boolean writeFile(final Path targetPath, final String content)
            throws IOException {
        if (targetPath == null) {
            throw new IllegalArgumentException("Target path cannot be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        lock.writeLock().lock();
        try {
            Path resolvedPath = handleCollision(targetPath);
            if (resolvedPath == null) {
                LOGGER.debug("Write skipped for file: {}", targetPath);
                return false;
            }
            return writeAtomically(resolvedPath, content);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Atomically writes bytes to a file using the configured collision
     * policy.
     *
     * @param targetPath the target file path
     * @param content the byte content to write
     * @return true if write succeeded, false if skipped due to collision
     * @throws IOException if an I/O error occurs during write
     */
    public boolean writeFile(final Path targetPath, final byte[] content)
            throws IOException {
        if (targetPath == null) {
            throw new IllegalArgumentException("Target path cannot be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        lock.writeLock().lock();
        try {
            Path resolvedPath = handleCollision(targetPath);
            if (resolvedPath == null) {
                LOGGER.debug("Write skipped for file: {}", targetPath);
                return false;
            }
            return writeAtomically(resolvedPath, content);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets the path of the last successfully written file.
     *
     * @return the last written path, or null if no writes have succeeded
     */
    public Path getLastWrittenPath() {
        lock.readLock().lock();
        try {
            return lastWrittenPath;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Handles file collision based on configured policy.
     *
     * @param targetPath the target file path
     * @return the path to write to, or null if write should be skipped
     */
    private Path handleCollision(final Path targetPath) {
        if (!Files.exists(targetPath)) {
            return targetPath;
        }

        // Java 17: Traditional switch statement (Java 21 used switch expressions)
        switch (policy) {
            case OVERWRITE:
                LOGGER.debug("Overwriting existing file: {}", targetPath);
                return targetPath;
            case SKIP:
                LOGGER.debug("Skipping write - file exists: {}",
                        targetPath);
                return null;
            case SUFFIX:
                return generateSuffixedPath(targetPath);
            default:
                throw new IllegalArgumentException(
                    "Unknown collision policy: " + policy);
        }
    }

    /**
     * Generates a new path with a numeric suffix when collision detected.
     *
     * @param originalPath the original file path
     * @return a non-existing path with numeric suffix
     */
    private Path generateSuffixedPath(final Path originalPath) {
        String filename = originalPath.getFileName().toString();
        int lastDot = filename.lastIndexOf('.');
        String baseName;
        String extension;

        if (lastDot > 0) {
            baseName = filename.substring(0, lastDot);
            extension = filename.substring(lastDot);
        } else {
            baseName = filename;
            extension = "";
        }

        Path parent = originalPath.getParent();
        for (int i = 1; i < MAX_SUFFIX_ATTEMPTS; i++) {
            String newFilename = baseName + "_" + i + extension;
            Path newPath = parent != null
                    ? parent.resolve(newFilename)
                    : Path.of(newFilename);

            if (!Files.exists(newPath)) {
                LOGGER.debug("Generated suffixed path: {}", newPath);
                return newPath;
            }
        }

        LOGGER.warn(
                "Could not find available filename after {} attempts",
                MAX_SUFFIX_ATTEMPTS);
        return originalPath;
    }

    /**
     * Performs atomic write using temp-file-then-move pattern.
     *
     * @param targetPath the target file path
     * @param content the string content to write
     * @return true if write succeeded
     * @throws IOException if an I/O error occurs
     */
    private boolean writeAtomically(final Path targetPath,
            final String content) throws IOException {
        Path tempPath = generateTempPath(targetPath);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.writeString(tempPath, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

            Files.move(tempPath, targetPath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            this.lastWrittenPath = targetPath;
            LOGGER.debug("Successfully wrote file: {}", targetPath);
            return true;
        } catch (final IOException e) {
            LOGGER.error("Failed to write file: {}", targetPath, e);
            Files.deleteIfExists(tempPath);
            throw e;
        }
    }

    /**
     * Performs atomic write of bytes using temp-file-then-move pattern.
     *
     * @param targetPath the target file path
     * @param content the byte content to write
     * @return true if write succeeded
     * @throws IOException if an I/O error occurs
     */
    private boolean writeAtomically(final Path targetPath,
            final byte[] content) throws IOException {
        Path tempPath = generateTempPath(targetPath);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.write(tempPath, content, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

            Files.move(tempPath, targetPath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            this.lastWrittenPath = targetPath;
            LOGGER.debug("Successfully wrote file: {}", targetPath);
            return true;
        } catch (final IOException e) {
            LOGGER.error("Failed to write file: {}", targetPath, e);
            Files.deleteIfExists(tempPath);
            throw e;
        }
    }

    /**
     * Generates a temporary file path in the same directory as target.
     *
     * @param targetPath the target file path
     * @return a temp file path
     */
    private Path generateTempPath(final Path targetPath) {
        return targetPath.resolveSibling(targetPath.getFileName() + TEMP_FILE_SUFFIX);
    }
}
