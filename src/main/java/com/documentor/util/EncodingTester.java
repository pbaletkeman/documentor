package com.documentor.util;

import java.nio.charset.Charset;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * A simple utility to test and verify UTF-8 encoding in the console and files.
 */
public class EncodingTester {

    private static final String[] TEST_SYMBOLS = {
        "✅", "❌", "⚠️", "ℹ️", "🔍", "📂", "📄", "🔧", "⚙️", "🔄", "▶️",
        "⏸️", "⏹️", "✨", "🔒", "🔓", "📊", "📈", "⭐", "❤️", "✔️"
    };

    public static void main(String[] args) {
        System.out.println("Documentor Encoding Test Utility");
        System.out.println("==============================");

        // Display system encoding information
        printEncodingInfo();

        // Test console output with special characters
        testConsoleOutput();

        // Test file writing with special characters
        testFileWriting();

        System.out.println("\nEncoding test completed.");
    }

    private static void printEncodingInfo() {
        System.out.println("\nSystem Encoding Information:");
        System.out.println("---------------------------");
        System.out.println("Default Charset: " + Charset.defaultCharset());
        System.out.println("File Encoding: " + System.getProperty("file.encoding"));
        System.out.println("Console Encoding: " + System.console() != null ?
                           "Available" : "Not available (running from IDE or redirected)");
        System.out.println("OS Name: " + System.getProperty("os.name"));
        System.out.println("Java Version: " + System.getProperty("java.version"));
    }

    private static void testConsoleOutput() {
        System.out.println("\nTesting Console Output:");
        System.out.println("----------------------");

        System.out.println("If you see symbols below, encoding is working correctly:");

        // Print all test symbols
        for (String symbol : TEST_SYMBOLS) {
            System.out.print(symbol + " ");
        }
        System.out.println("\n");

        // Print in a sentence context
        System.out.println("Task completed " + TEST_SYMBOLS[0]);
        System.out.println("Error occurred " + TEST_SYMBOLS[1]);
        System.out.println("Warning: file not found " + TEST_SYMBOLS[2]);
        System.out.println("Information " + TEST_SYMBOLS[3] + " This is working correctly");
    }

    private static void testFileWriting() {
        System.out.println("\nTesting File Writing:");
        System.out.println("-------------------");

        try {
            // Create a test file with UTF-8 encoding
            File testFile = new File("encoding-test.txt");
            try (PrintWriter writer = new PrintWriter(testFile, "UTF-8")) {
                writer.println("Documentor Encoding Test File");
                writer.println("============================");
                writer.println();

                writer.println("This file contains UTF-8 encoded special characters:");
                writer.println();

                // Write all test symbols to file
                for (String symbol : TEST_SYMBOLS) {
                    writer.print(symbol + " ");
                }
                writer.println("\n");

                // Write in a sentence context
                writer.println("Task completed " + TEST_SYMBOLS[0]);
                writer.println("Error occurred " + TEST_SYMBOLS[1]);
                writer.println("Warning: file not found " + TEST_SYMBOLS[2]);
                writer.println("Information " + TEST_SYMBOLS[3] + " This is working correctly");
            }

            System.out.println("Created file: " + testFile.getAbsolutePath());

            // Read the file back to verify encoding
            System.out.println("Reading file back to verify encoding:");
            byte[] bytes = Files.readAllBytes(Paths.get(testFile.getAbsolutePath()));
            String content = new String(bytes, "UTF-8");

            // Print just the first few lines to avoid overwhelming the console
            String[] lines = content.split("\n");
            for (int i = 0; i < Math.min(lines.length, 10); i++) {
                System.out.println("> " + lines[i]);
            }

            System.out.println("\nFile encoding test successful!");

        } catch (Exception e) {
            System.out.println("Error during file writing test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
