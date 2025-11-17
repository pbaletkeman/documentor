import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class FixBomEncoding {
    public static void main(String[] args) {
        String[] filesToFix = {
            "src/main/java/com/documentor/cli/DirectCommandProcessor.java",
            "src/main/java/com/documentor/cli/DocumentorCommands.java",
            "src/main/java/com/documentor/cli/handlers/CommonCommandHandler.java",
            "src/main/java/com/documentor/cli/handlers/ConfigurationCommandHandler.java",
            "src/main/java/com/documentor/cli/handlers/EnhancedProjectAnalysisHandler.java",
            "src/main/java/com/documentor/cli/handlers/ProjectAnalysisCommandHandler.java",
            "src/main/java/com/documentor/cli/handlers/ProjectAnalysisRequest.java",
            "src/main/java/com/documentor/cli/handlers/StatusCommandHandler.java",
            "src/main/java/com/documentor/config/AppConfig.java",
            "src/main/java/com/documentor/config/AppConfigEnhanced.java",
            "src/main/java/com/documentor/config/BeanUtils.java",
            "src/main/java/com/documentor/config/DiagramServiceConfiguration.java",
            "src/main/java/com/documentor/config/DocumentationServiceConfiguration.java",
            "src/main/java/com/documentor/config/DocumentorConfig.java",
            "src/main/java/com/documentor/config/EarlyConfigurationLoader.java",
            "src/main/java/com/documentor/config/ExternalConfigLoader.java",
            "src/main/java/com/documentor/config/LlmServiceConfiguration.java",
            "src/main/java/com/documentor/config/LlmServiceConfigurationEnhanced.java"
        };

        for (String filename : filesToFix) {
            try {
                Path path = Paths.get(filename);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                    if (!lines.isEmpty()) {
                        String firstLine = lines.get(0);
                        // Fix BOM and missing 'p' in package
                        if (firstLine.startsWith("\ufeffackage")) {
                            lines.set(0, firstLine.replace("\ufeffackage", "package"));
                            Files.write(path, lines, StandardCharsets.UTF_8);
                            System.out.println("Fixed: " + filename);
                        } else if (firstLine.startsWith("ackage")) {
                            lines.set(0, "package" + firstLine.substring(6));
                            Files.write(path, lines, StandardCharsets.UTF_8);
                            System.out.println("Fixed: " + filename);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error fixing " + filename + ": " + e.getMessage());
            }
        }
        System.out.println("BOM fix complete!");
    }
}
