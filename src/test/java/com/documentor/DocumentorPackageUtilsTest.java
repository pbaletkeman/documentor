package com.documentor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class DocumentorPackageUtilsTest {

    private String originalSpringProfile;

    @BeforeEach
    void setUp() {
        // Save original system property
        originalSpringProfile = System.getProperty("spring.profiles.active");
    }

    @AfterEach
    void tearDown() {
        // Restore original system property
        if (originalSpringProfile != null) {
            System.setProperty("spring.profiles.active",
                originalSpringProfile);
        } else {
            System.clearProperty("spring.profiles.active");
        }
    }

    @Test
    void testConstants() {
        // Test all public constants
        assertEquals("Documentor",
            DocumentorPackageUtils.APPLICATION_NAME);
        assertEquals("1.0.0",
            DocumentorPackageUtils.VERSION);
        assertEquals("com.documentor",
            DocumentorPackageUtils.PACKAGE_NAME);
        assertEquals("config.json",
            DocumentorPackageUtils.DEFAULT_CONFIG_FILE);
        assertEquals("config-test.json",
            DocumentorPackageUtils.TEST_CONFIG_FILE);
    }

    @Test
    void testConstructorThrowsException() {
        // Test that the private constructor throws
        // UnsupportedOperationException
        Exception exception = assertThrows(Exception.class, () -> {
            // Use reflection to access private constructor
            var constructor =
                DocumentorPackageUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // The exception should be either UnsupportedOperationException
        // directly or wrapped in InvocationTargetException
        assertTrue(exception instanceof UnsupportedOperationException
                || (exception.getCause()
                instanceof
                   UnsupportedOperationException),
                   "Expected UnsupportedOperationException but got: "
                   + exception.getClass());
    }

    @Test
    void testGetApplicationIdentifier() {
        String identifier = DocumentorPackageUtils.getApplicationIdentifier();
        assertEquals("Documentor-1.0.0", identifier);

        // Verify it's consistent
        assertEquals(identifier, DocumentorPackageUtils
            .getApplicationIdentifier());
    }

    @Test
    void testIsValidConfigFileName() {
        // Test valid file names
        assertTrue(DocumentorPackageUtils
            .isValidConfigFileName("config.json"));
        assertTrue(DocumentorPackageUtils
            .isValidConfigFileName("app.yml"));
        assertTrue(DocumentorPackageUtils
            .isValidConfigFileName("settings.yaml"));
        assertTrue(DocumentorPackageUtils
            .isValidConfigFileName("test-config.json"));
        assertTrue(DocumentorPackageUtils
            .isValidConfigFileName("application.yml"));
        assertTrue(DocumentorPackageUtils
            .isValidConfigFileName("bootstrap.yaml"));

        // Test invalid file names
        assertFalse(DocumentorPackageUtils
            .isValidConfigFileName(null));
        assertFalse(DocumentorPackageUtils
            .isValidConfigFileName(""));
        assertFalse(DocumentorPackageUtils
            .isValidConfigFileName("   "));
        assertFalse(DocumentorPackageUtils
            .isValidConfigFileName("config.txt"));
        assertFalse(DocumentorPackageUtils
            .isValidConfigFileName("config.xml"));
        assertFalse(DocumentorPackageUtils
            .isValidConfigFileName("config"));
        assertFalse(DocumentorPackageUtils
            .isValidConfigFileName("config.properties"));
    }

    @Test
    void testNormalizePackageName() {
        // Test null input
        assertEquals("com.documentor",
            DocumentorPackageUtils.normalizePackageName(null));

        // Test empty inputs
        assertEquals("com.documentor",
            DocumentorPackageUtils.normalizePackageName(""));
        assertEquals("com.documentor",
            DocumentorPackageUtils.normalizePackageName("   "));

        // Test package names that already start with base package
        assertEquals("com.documentor.service",
            DocumentorPackageUtils.normalizePackageName(
                "com.documentor.service"));
        assertEquals("com.documentor.cli",
            DocumentorPackageUtils.normalizePackageName("com.documentor.cli"));
        assertEquals("com.documentor",
            DocumentorPackageUtils.normalizePackageName("com.documentor"));

        // Test package names that need to be prefixed
        assertEquals("com.documentor.service",
            DocumentorPackageUtils.normalizePackageName("service"));
        assertEquals("com.documentor.cli.commands",
            DocumentorPackageUtils.normalizePackageName("cli.commands"));
        assertEquals("com.documentor.util",
            DocumentorPackageUtils.normalizePackageName("util"));

        // Test with leading/trailing whitespace
        assertEquals("com.documentor.service",
            DocumentorPackageUtils.normalizePackageName("  service  "));
        assertEquals("com.documentor.cli",
            DocumentorPackageUtils.normalizePackageName(
                " com.documentor.cli "));
    }

    @Test
    void testGetVersion() {
        assertEquals("1.0.0", DocumentorPackageUtils.getVersion());

        // Verify consistency
        assertEquals(DocumentorPackageUtils.VERSION,
            DocumentorPackageUtils.getVersion());
    }

    @Test
    void testGetApplicationName() {
        assertEquals("Documentor",
            DocumentorPackageUtils.getApplicationName());

        // Verify consistency
        assertEquals(DocumentorPackageUtils.APPLICATION_NAME,
            DocumentorPackageUtils.getApplicationName());
    }

    @Test
    void testIsTestMode() {
        // Test without any spring profile set
        System.clearProperty("spring.profiles.active");
        assertFalse(DocumentorPackageUtils.isTestMode());

        // Test with test profile
        System.setProperty("spring.profiles.active", "test");
        assertTrue(DocumentorPackageUtils.isTestMode());

        // Test with testing profile
        System.setProperty("spring.profiles.active", "testing");
        assertTrue(DocumentorPackageUtils.isTestMode());

        // Test with other profiles
        System.setProperty("spring.profiles.active", "dev");
        assertFalse(DocumentorPackageUtils.isTestMode());

        System.setProperty("spring.profiles.active", "prod");
        assertFalse(DocumentorPackageUtils.isTestMode());

        System.setProperty("spring.profiles.active", "local");
        assertFalse(DocumentorPackageUtils.isTestMode());

        // Test with empty profile
        System.setProperty("spring.profiles.active", "");
        assertFalse(DocumentorPackageUtils.isTestMode());
    }

    @Test
    void testGetDefaultConfigPath() {
        // Test in non-test mode
        System.clearProperty("spring.profiles.active");
        assertEquals("config.json",
        DocumentorPackageUtils.getDefaultConfigPath());

        System.setProperty("spring.profiles.activ   e", "dev");
        assertEquals("config.json",
        DocumentorPackageUtils.getDefaultConfigPath());

        // Test in test mode
        System.setProperty("spring.profiles.active", "test");
        assertEquals("config-test.json",
            DocumentorPackageUtils.getDefaultConfigPath());

        System.setProperty("spring.profiles.active", "testing");
        assertEquals("config-test.json",
            DocumentorPackageUtils.getDefaultConfigPath());
    }

    @Test
    void testConstantsAreNotNull() {
        // Ensure all constants are properly initialized
        assertNotNull(DocumentorPackageUtils.APPLICATION_NAME);
        assertNotNull(DocumentorPackageUtils.VERSION);
        assertNotNull(DocumentorPackageUtils.PACKAGE_NAME);
        assertNotNull(DocumentorPackageUtils.DEFAULT_CONFIG_FILE);
        assertNotNull(DocumentorPackageUtils.TEST_CONFIG_FILE);

        // Ensure they're not empty
        assertFalse(DocumentorPackageUtils.APPLICATION_NAME.isEmpty());
        assertFalse(DocumentorPackageUtils.VERSION.isEmpty());
        assertFalse(DocumentorPackageUtils.PACKAGE_NAME.isEmpty());
        assertFalse(DocumentorPackageUtils.DEFAULT_CONFIG_FILE.isEmpty());
        assertFalse(DocumentorPackageUtils.TEST_CONFIG_FILE.isEmpty());
    }

    @Test
    void testMethodsReturnConsistentValues() {
        // Test that multiple calls return the same values
        String id1 = DocumentorPackageUtils.getApplicationIdentifier();
        String id2 = DocumentorPackageUtils.getApplicationIdentifier();
        assertEquals(id1, id2);

        String version1 = DocumentorPackageUtils.getVersion();
        String version2 = DocumentorPackageUtils.getVersion();
        assertEquals(version1, version2);

        String name1 = DocumentorPackageUtils.getApplicationName();
        String name2 = DocumentorPackageUtils.getApplicationName();
        assertEquals(name1, name2);
    }

    @Test
    void testBoundaryConditionsForConfigFileName() {
        // Test very short valid names
        assertTrue(DocumentorPackageUtils.isValidConfigFileName("a.json"));
        assertTrue(DocumentorPackageUtils.isValidConfigFileName("b.yml"));
        assertTrue(DocumentorPackageUtils.isValidConfigFileName("c.yaml"));

        // Test names that almost match but don't
        assertFalse(DocumentorPackageUtils.isValidConfigFileName(
            "config.json.txt"));
        assertFalse(DocumentorPackageUtils.isValidConfigFileName("json"));

        // Test edge cases - these should be false since they start with dot
        assertFalse(DocumentorPackageUtils.isValidConfigFileName(".json"));
        assertFalse(DocumentorPackageUtils.isValidConfigFileName(".yml"));
        assertFalse(DocumentorPackageUtils.isValidConfigFileName(".yaml"));
    }

    @Test
    void testComplexPackageNameScenarios() {
        // Test deeply nested packages
        assertEquals("com.documentor.service.impl.advanced",
            DocumentorPackageUtils.normalizePackageName(
                "service.impl.advanced"));

        // Test packages that contain the base package in the middle
        assertEquals("com.documentor.other.com.documentor.service",
            DocumentorPackageUtils.normalizePackageName(
                "other.com.documentor.service"));

        // Test single character package names
        assertEquals("com.documentor.a",
            DocumentorPackageUtils.normalizePackageName("a"));
        assertEquals("com.documentor.a.b.c",
            DocumentorPackageUtils.normalizePackageName("a.b.c"));
    }
}
