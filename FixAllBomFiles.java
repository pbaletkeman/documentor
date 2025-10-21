import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

public class FixAllBomFiles {
    public static void main(String[] args) {
        try {
            Path rootPath = Paths.get("src");

            System.out.println("Scanning for Java files with BOM or corrupted package declarations...");

            try (Stream<Path> paths = Files.walk(rootPath)) {
                paths.filter(Files::isRegularFile)
                     .filter(path -> path.toString().endsWith(".java"))
                     .forEach(FixAllBomFiles::fixFile);
            }

            System.out.println("BOM fix complete for all Java files!");

        } catch (IOException e) {
            System.err.println("Error during fix: " + e.getMessage());
        }
    }

    private static void fixFile(Path path) {
        try {
            if (!Files.exists(path)) {
                return;
            }

            byte[] bytes = Files.readAllBytes(path);
            String content = new String(bytes, StandardCharsets.UTF_8);

            // Check if file has BOM or corrupted package line
            boolean needsFix = false;
            String[] lines = content.split("\n", -1);

            if (lines.length > 0) {
                String firstLine = lines[0];

                // Check for BOM + corrupted package
                if (firstLine.startsWith("\ufeffackage") || firstLine.startsWith("ackage") ||
                    firstLine.contains("\ufeff")) {
                    needsFix = true;

                    // Fix the first line
                    if (firstLine.startsWith("\ufeffackage")) {
                        lines[0] = firstLine.replace("\ufeffackage", "package");
                    } else if (firstLine.startsWith("ackage")) {
                        lines[0] = "package" + firstLine.substring(6);
                    } else if (firstLine.contains("\ufeff")) {
                        lines[0] = firstLine.replace("\ufeff", "");
                    }
                }
            }

            if (needsFix) {
                // Rebuild content
                String fixedContent = String.join("\n", lines);

                // Write back without BOM
                Files.write(path, fixedContent.getBytes(StandardCharsets.UTF_8));
                System.out.println("Fixed: " + path);
            }

        } catch (IOException e) {
            System.err.println("Error fixing " + path + ": " + e.getMessage());
        }
    }
}
