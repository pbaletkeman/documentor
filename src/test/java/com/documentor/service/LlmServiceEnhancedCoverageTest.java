package com.documentor.service;

import com.documentor.config.DocumentorConfig;
import com.documentor.service.llm.LlmApiClient;
import com.documentor.service.llm.LlmRequestBuilder;
import com.documentor.service.llm.LlmResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Simple coverage enhancement tests for LlmServiceEnhanced.
 * Focuses on testing basic functionality since most missing coverage
 * is in private methods and complex integration scenarios.
 */
@ExtendWith(MockitoExtension.class)
class LlmServiceEnhancedCoverageTest {

    @Mock
    private DocumentorConfig mockConfig;

    @Mock
    private LlmRequestBuilder mockRequestBuilder;

    @Mock
    private LlmResponseHandler mockResponseHandler;

    @Mock
    private LlmApiClient mockApiClient;

    private LlmServiceEnhanced llmServiceEnhanced;

    @BeforeEach
    void setUp() {
        llmServiceEnhanced = new LlmServiceEnhanced(
            mockConfig, mockRequestBuilder, mockResponseHandler, mockApiClient);
    }

    @Test
    void testThreadLocalConfigOperations() {
        // Test thread local configuration operations using static methods
        DocumentorConfig testConfig = mockConfig;

        LlmServiceEnhanced.setThreadLocalConfig(testConfig);
        DocumentorConfig retrieved = LlmServiceEnhanced.getThreadLocalConfig();

        assertEquals(testConfig, retrieved);

        LlmServiceEnhanced.clearThreadLocalConfig();
        DocumentorConfig afterClear = LlmServiceEnhanced.getThreadLocalConfig();

        assertNull(afterClear);
    }

    @Test
    void testServiceInstantiation() {
        // Test that the service can be instantiated properly
        assertNotNull(llmServiceEnhanced);

        // This mainly tests the constructor and basic setup
        // The actual coverage improvement will come from the other services
        // that have more accessible public methods
    }
}
