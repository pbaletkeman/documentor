package com.documentor;

/**
 * Package utility constants and helper methods for Documentor applications.
 * This class provides common constants and utility methods used across different
 * Documentor application variants.
 */
public final class DocumentorPackageUtils {

    /**
     * Application name constant.
     */
    public static final String APPLICATION_NAME = "Documentor";

    /**
     * Version constant.
     */
    public static final String VERSION = "1.0.0";

    /**
     * Package name constant.
     */
    public static final String PACKAGE_NAME = "com.documentor";

    /**
     * Default configuration file name.
     */
    public static final String DEFAULT_CONFIG_FILE = "config.json";

    /**
     * Test configuration file name.
     */
    public static final String TEST_CONFIG_FILE = "config-test.json";

    /**
     * Private constructor to prevent instantiation.
     */
    private DocumentorPackageUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Gets the full application identifier.
     *
     * @return the application identifier in format "name-version"
     */
    public static String getApplicationIdentifier() {
        return APPLICATION_NAME + "-" + VERSION;
    }

    /**
     * Checks if a given string is a valid configuration file name.
     *
     * @param fileName the file name to check
     * @return true if the file name is valid for configuration
     */
    public static boolean isValidConfigFileName(final String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        String trimmed = fileName.trim();

        // Must have at least one character before the extension
        if (trimmed.startsWith(".")) {
            return false;
        }

        return trimmed.endsWith(".json") || trimmed.endsWith(".yml") || trimmed.endsWith(".yaml");
    }

    /**
     * Normalizes a package name by ensuring it starts with the base package.
     *
     * @param packageName the package name to normalize
     * @return the normalized package name
     */
    public static String normalizePackageName(final String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            return PACKAGE_NAME;
        }

        String trimmed = packageName.trim();
        if (trimmed.startsWith(PACKAGE_NAME)) {
            return trimmed;
        }

        return PACKAGE_NAME + "." + trimmed;
    }

    /**
     * Gets the application version.
     *
     * @return the application version
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Gets the application name.
     *
     * @return the application name
     */
    public static String getApplicationName() {
        return APPLICATION_NAME;
    }

    /**
     * Checks if the application is running in test mode based on system properties.
     *
     * @return true if running in test mode
     */
    public static boolean isTestMode() {
        String testProfile = System.getProperty("spring.profiles.active");
        return "test".equals(testProfile) || "testing".equals(testProfile);
    }

    /**
     * Gets the default configuration file path for the current mode.
     *
     * @return the default configuration file path
     */
    public static String getDefaultConfigPath() {
        return isTestMode() ? TEST_CONFIG_FILE : DEFAULT_CONFIG_FILE;
    }
}
