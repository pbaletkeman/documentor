package com.documentor.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import com.documentor.config.model.OutputSettings;

/**
 * Additional tests to improve branch coverage for remaining config classes.
 * Focuses on edge cases and error conditions to cover more branches.
 */
@ExtendWith(MockitoExtension.class)
class ConfigPackageAdditionalTest {

    @Test
    void testThreadLocalContextHolderBoundaryConditions() {
        // Test clear when already clear
        // (covers branch for when threadLocal is already null)
        ThreadLocalContextHolder.clearConfig();
        assertNull(ThreadLocalContextHolder.getConfig());
        assertFalse(ThreadLocalContextHolder.isConfigExplicitlySet());

        // Test setting null config
        ThreadLocalContextHolder.setConfig(null);
        assertNull(ThreadLocalContextHolder.getConfig());

        // Test setting actual config
        DocumentorConfig config = new DocumentorConfig(
            java.util.List.of(),
            new OutputSettings("/test", "MARKDOWN", false, false, false,
                null, null, null, null),
            null
        );
        ThreadLocalContextHolder.setConfig(config);
        assertNotNull(ThreadLocalContextHolder.getConfig());

        // Test multiple clears
        ThreadLocalContextHolder.clearConfig();
        ThreadLocalContextHolder.clearConfig();
        assertNull(ThreadLocalContextHolder.getConfig());
    }

    @Test
    void testThreadLocalPropagatingExecutorBoundaryConditions() {
        // Test createExecutor with zero threads
        var executor1 = ThreadLocalPropagatingExecutorEnhanced
            .createExecutor(0, "zero-threads");
        assertNotNull(executor1);

        // Test createExecutor with negative threads
            final int negativeThreadCount = -5;
        var executor2 = ThreadLocalPropagatingExecutorEnhanced
                .createExecutor(negativeThreadCount, "negative-threads");
        assertNotNull(executor2);

        // Test createExecutor with very high thread count
            final int highThreadCount = 1000;
        var executor3 = ThreadLocalPropagatingExecutorEnhanced
                .createExecutor(highThreadCount, "high-threads");
        assertNotNull(executor3);
    }

    @Test
    void testEarlyConfigurationLoaderEdgeCases() {
        // Test constructor with non-null config loader
        ExternalConfigLoader mockLoader =
            org.mockito.Mockito.mock(ExternalConfigLoader.class);
        EarlyConfigurationLoader loader =
            new EarlyConfigurationLoader(mockLoader);
        assertNotNull(loader);

        // Test run method with mock arguments
        org.springframework.boot.ApplicationArguments mockArgs =
            org.mockito.Mockito.mock(org.springframework.boot
            .ApplicationArguments.class);
        org.mockito.Mockito.when(mockArgs.getSourceArgs())
            .thenReturn(new String[]{"analyze"});

        assertDoesNotThrow(() -> loader.run(mockArgs));
    }

    @Test
    void testExternalConfigLoaderBoundaryConditions() {
        ExternalConfigLoader loader = new ExternalConfigLoader();

        // Test loadExternalConfig with null args
        boolean result1 = loader.loadExternalConfig(null);
        assertFalse(result1);

        // Test loadExternalConfig with empty args
        boolean result2 = loader.loadExternalConfig(new String[0]);
        assertFalse(result2);

        // Test getLoadedConfig when no config loaded
        DocumentorConfig config = loader.getLoadedConfig();
        assertNull(config);

        // Test with single argument (no config flag)
        boolean result3 = loader.loadExternalConfig(new String[]{"analyze"});
        assertFalse(result3);
    }

    @Test
    void testLlmServiceConfigurationBasic() {
        // These configuration classes are Spring @Configuration classes,
        // test reflection access
        Class<?> llmServiceConfig = LlmServiceConfiguration.class;
        assertNotNull(llmServiceConfig);
        assertTrue(llmServiceConfig.isAnnotationPresent(
            org.springframework.context
            .annotation.Configuration.class));

        Class<?> llmServiceConfigEnhanced =
            LlmServiceConfigurationEnhanced.class;
        assertNotNull(llmServiceConfigEnhanced);
        assertTrue(llmServiceConfigEnhanced.isAnnotationPresent(
            org.springframework.context
                .annotation.Configuration.class));
    }

    @Test
    void testDiagramServiceConfigurationBranches() {
        // Test DiagramServiceConfiguration constructor
        assertDoesNotThrow(() -> {
            DiagramServiceConfiguration config =
                new DiagramServiceConfiguration();
            assertNotNull(config);
        });
    }

    @Test
    void testDocumentationServiceConfigurationBranches() {
        // Test DocumentationServiceConfiguration constructor
        assertDoesNotThrow(() -> {
            DocumentationServiceConfiguration config =
                new DocumentationServiceConfiguration();
            assertNotNull(config);
        });
    }
}
