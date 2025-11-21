package com.documentor.service.llm.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Mock LLM Provider Tests")
class MockLlmProviderTest {
    private static final int LONG_CONTENT_LENGTH = 1000;

    @Nested
    @DisplayName("MockOpenAiProvider Tests")
    class MockOpenAiProviderTests {
        private MockLlmProvider provider;

        @BeforeEach
        void setUp() {
            provider = new MockOpenAiProvider();
        }

        @Test
        @DisplayName("should return openai provider name")
        void testProviderName() {
            assertEquals("openai", provider.getProviderName());
        }

        @Test
        @DisplayName("should use default model gpt-3.5-turbo")
        void testDefaultModel() {
            assertEquals("gpt-3.5-turbo", provider.getDefaultModel());
        }

        @Test
        @DisplayName("should complete simple prompt")
        void testComplete() {
            String response = provider.complete("What is Java?");
            assertNotNull(response);
            assertTrue(response.contains("Mock OpenAI"));
        }

        @Test
        @DisplayName("should return code example for code prompts")
        void testCodePrompt() {
            String response = provider.complete("Write Java code");
            assertNotNull(response);
            assertTrue(response.contains("java") || response.contains("Java"));
        }

        @Test
        @DisplayName("should handle empty prompt")
        void testEmptyPrompt() {
            String response = provider.complete("");
            assertNotNull(response);
            assertTrue(response.contains("Empty prompt"));
        }

        @Test
        @DisplayName("should support chat interface")
        void testChat() {
            List<MockLlmProvider.ChatMessage> messages = List.of(
                    new MockLlmProvider.ChatMessage(
                        "user", "Explain Spring Boot"));

            String response = provider.chat(messages);
            assertNotNull(response);
            assertTrue(response.contains("Mock OpenAI"));
        }

        @Test
        @DisplayName("should be available")
        void testAvailable() {
            assertTrue(provider.isAvailable());
        }

        @Test
        @DisplayName("should support custom model")
        void testCustomModel() {
            provider = new MockOpenAiProvider("gpt-4");
            String response = provider.complete("Test", "gpt-4");
            assertTrue(response.contains("Mock OpenAI"));
        }

        @Test
        @DisplayName("should allow setting default model")
        void testSetDefaultModel() {
            provider.setDefaultModel("gpt-4");
            assertEquals("gpt-4", provider.getDefaultModel());
        }
    }

    @Nested
    @DisplayName("MockOllamaProvider Tests")
    class MockOllamaProviderTests {
        private MockLlmProvider provider;

        @BeforeEach
        void setUp() {
            provider = new MockOllamaProvider();
        }

        @Test
        @DisplayName("should return ollama provider name")
        void testProviderName() {
            assertEquals("ollama", provider.getProviderName());
        }

        @Test
        @DisplayName("should use default model llama2")
        void testDefaultModel() {
            assertEquals("llama2", provider.getDefaultModel());
        }

        @Test
        @DisplayName("should complete simple prompt")
        void testComplete() {
            String response = provider.complete("What is Python?");
            assertNotNull(response);
            assertTrue(response.contains("Mock Ollama"));
        }

        @Test
        @DisplayName("should return local efficiency"
            + " for local deployment prompts")
        void testLocalPrompt() {
            String response = provider.complete("document this code locally");
            assertNotNull(response);
            assertTrue(response.contains("local")
            || response.contains("Ollama"));
        }

        @Test
        @DisplayName("should handle chat messages")
        void testChat() {
            List<MockLlmProvider.ChatMessage> messages = List.of(
                    new MockLlmProvider.ChatMessage(
                        "system", "You are helpful"),
                    new MockLlmProvider.ChatMessage(
                        "user", "Explain Gradle"));

            String response = provider.chat(messages);
            assertNotNull(response);
            assertTrue(response.contains("Mock Ollama"));
        }

        @Test
        @DisplayName("should be available")
        void testAvailable() {
            assertTrue(provider.isAvailable());
        }

        @Test
        @DisplayName("should support custom model")
        void testCustomModel() {
            provider = new MockOllamaProvider("neural-chat");
            String response = provider.complete("Test", "neural-chat");
            assertTrue(response.contains("Mock Ollama"));
        }
    }

    @Nested
    @DisplayName("MockLlamaCppProvider Tests")
    class MockLlamaCppProviderTests {
        private MockLlmProvider provider;

        @BeforeEach
        void setUp() {
            provider = new MockLlamaCppProvider();
        }

        @Test
        @DisplayName("should return llamacpp provider name")
        void testProviderName() {
            assertEquals("llamacpp", provider.getProviderName());
        }

        @Test
        @DisplayName("should use default model llama-7b-gguf")
        void testDefaultModel() {
            assertEquals("llama-7b-gguf", provider.getDefaultModel());
        }

        @Test
        @DisplayName("should complete simple prompt")
        void testComplete() {
            String response = provider.complete("Explain containers");
            assertNotNull(response);
            assertTrue(response.contains("Mock llama.cpp"));
        }

        @Test
        @DisplayName("should mention CPU efficiency for llamacpp")
        void testCpuEfficiency() {
            String response = provider.complete("Optimize this code");
            assertNotNull(response);
            assertTrue(response.contains("llama.cpp")
                || response.contains("CPU"));
        }

        @Test
        @DisplayName("should support chat interface")
        void testChat() {
            List<MockLlmProvider.ChatMessage> messages = List.of(
                    new MockLlmProvider.ChatMessage("user",
                            "Generate documentation"));

            String response = provider.chat(messages);
            assertNotNull(response);
            assertTrue(response.contains("Mock llama.cpp"));
        }

        @Test
        @DisplayName("should be available")
        void testAvailable() {
            assertTrue(provider.isAvailable());
        }

        @Test
        @DisplayName("should support model variant")
        void testModelVariant() {
            provider = new MockLlamaCppProvider("llama-13b-gguf");
            assertEquals("llama-13b-gguf", provider.getDefaultModel());
        }
    }

    @Nested
    @DisplayName("MockLlmProviderFactory Tests")
    class MockLlmProviderFactoryTests {
        @AfterEach
        void tearDown() {
            MockLlmProviderFactory.clearCache();
        }

        @Test
        @DisplayName("should create OpenAI provider")
        void testCreateOpenAiProvider() {
            MockLlmProvider provider = MockLlmProviderFactory
                    .createProvider(MockLlmProviderFactory.ProviderType.OPENAI);
            assertNotNull(provider);
            assertEquals("openai", provider.getProviderName());
        }

        @Test
        @DisplayName("should create Ollama provider")
        void testCreateOllamaProvider() {
            MockLlmProvider provider = MockLlmProviderFactory
                    .createProvider(MockLlmProviderFactory.ProviderType.OLLAMA);
            assertNotNull(provider);
            assertEquals("ollama", provider.getProviderName());
        }

        @Test
        @DisplayName("should create llama.cpp provider")
        void testCreateLlamaCppProvider() {
            MockLlmProvider provider = MockLlmProviderFactory
                    .createProvider(
                        MockLlmProviderFactory.ProviderType.LLAMACPP);
            assertNotNull(provider);
            assertEquals("llamacpp", provider.getProviderName());
        }

        @Test
        @DisplayName("should create provider from string name")
        void testCreateFromString() {
            MockLlmProvider provider = MockLlmProviderFactory
                .createProvider("openai");
            assertNotNull(provider);
            assertEquals("openai", provider.getProviderName());
        }

        @Test
        @DisplayName("should create provider with custom model")
        void testCreateWithModel() {
            MockLlmProvider provider = MockLlmProviderFactory
                    .createProvider(
                            MockLlmProviderFactory.ProviderType.OPENAI,
                            "gpt-4");
            assertNotNull(provider);
            assertEquals("gpt-4", provider.getDefaultModel());
        }

        @Test
        @DisplayName("should throw exception for null provider type")
        void testNullProviderType() {
            assertThrows(IllegalArgumentException.class,
                    () -> MockLlmProviderFactory.createProvider(
                            (MockLlmProviderFactory.ProviderType) null));
        }

        @Test
        @DisplayName("should throw exception for unknown provider string")
        void testUnknownProvider() {
            assertThrows(IllegalArgumentException.class,
                    () -> MockLlmProviderFactory.createProvider("unknown"));
        }

        @Test
        @DisplayName("should cache provider instances")
        void testCaching() {
            MockLlmProvider provider1 = MockLlmProviderFactory
                    .getProvider(MockLlmProviderFactory.ProviderType.OPENAI);
            MockLlmProvider provider2 = MockLlmProviderFactory
                    .getProvider(MockLlmProviderFactory.ProviderType.OPENAI);

            assertEquals(provider1, provider2);
            assertEquals(1, MockLlmProviderFactory.getCacheSize());
        }

        @Test
        @DisplayName("should clear cache")
        void testClearCache() {
            MockLlmProviderFactory.getProvider("openai");
            MockLlmProviderFactory.getProvider("ollama");

            assertEquals(2, MockLlmProviderFactory.getCacheSize());
            MockLlmProviderFactory.clearCache();
            assertEquals(0, MockLlmProviderFactory.getCacheSize());
        }

        @Test
        @DisplayName("should remove specific provider from cache")
        void testRemoveFromCache() {
            MockLlmProviderFactory.getProvider("openai");
            MockLlmProviderFactory.getProvider("ollama");

            assertEquals(2, MockLlmProviderFactory.getCacheSize());
            MockLlmProviderFactory
                .removeFromCache(MockLlmProviderFactory.ProviderType.OPENAI);
            assertEquals(1, MockLlmProviderFactory.getCacheSize());
        }

        @Test
        @DisplayName("should support case-insensitive provider name")
        void testCaseInsensitiveProvider() {
            MockLlmProvider provider1 = MockLlmProviderFactory
                .createProvider("OPENAI");
            MockLlmProvider provider2 = MockLlmProviderFactory
                .createProvider("OpenAI");

            assertEquals("openai", provider1.getProviderName());
            assertEquals("openai", provider2.getProviderName());
        }

        @Test
        @DisplayName("should get provider from string name")
        void testGetProviderFromString() {
            MockLlmProvider provider =
                MockLlmProviderFactory.getProvider("ollama");
            assertNotNull(provider);
            assertEquals("ollama", provider.getProviderName());
        }

        @Test
        @DisplayName("should throw exception for null provider name")
        void testNullProviderName() {
            assertThrows(IllegalArgumentException.class,
                    () -> MockLlmProviderFactory.createProvider((String) null));
        }

        @Test
        @DisplayName("should throw exception for empty provider name")
        void testEmptyProviderName() {
            assertThrows(IllegalArgumentException.class,
                    () -> MockLlmProviderFactory.createProvider(""));
        }

        @Test
        @DisplayName("should support different models for same provider")
        void testMultipleModelsPerProvider() {
            MockLlmProvider openai1 = MockLlmProviderFactory
                    .getProvider("openai", "gpt-3.5-turbo");
            MockLlmProvider openai2 = MockLlmProviderFactory
                    .getProvider("openai", "gpt-4");

            assertEquals(2, MockLlmProviderFactory.getCacheSize());
            assertEquals("gpt-3.5-turbo", openai1.getDefaultModel());
            assertEquals("gpt-4", openai2.getDefaultModel());
        }
    }

    @Nested
    @DisplayName("ChatMessage Tests")
    class ChatMessageTests {
        @Test
        @DisplayName("should create chat message with role and content")
        void testCreateChatMessage() {
            MockLlmProvider.ChatMessage message =
                    new MockLlmProvider.ChatMessage("user", "Hello");

            assertEquals("user", message.role());
            assertEquals("Hello", message.content());
        }

        @Test
        @DisplayName("should support different roles")
        void testDifferentRoles() {
            MockLlmProvider.ChatMessage userMsg =
                    new MockLlmProvider.ChatMessage("user", "Question");
            MockLlmProvider.ChatMessage assistantMsg =
                    new MockLlmProvider.ChatMessage("assistant", "Answer");
            MockLlmProvider.ChatMessage systemMsg =
                    new MockLlmProvider.ChatMessage("system", "Instructions");

            assertEquals("user", userMsg.role());
            assertEquals("assistant", assistantMsg.role());
            assertEquals("system", systemMsg.role());
        }

        @Test
        @DisplayName("should handle long content")
        void testLongContent() {
            String longContent = "x".repeat(LONG_CONTENT_LENGTH);
            MockLlmProvider.ChatMessage message =
                    new MockLlmProvider.ChatMessage("user", longContent);

            assertEquals(LONG_CONTENT_LENGTH, message.content().length());
        }
    }

    @Nested
    @DisplayName("Provider Integration Tests")
    class ProviderIntegrationTests {
        @Test
        @DisplayName("should work with mixed provider types")
        void testMixedProviders() {
            MockLlmProvider openai = new MockOpenAiProvider();
            MockLlmProvider ollama = new MockOllamaProvider();
            MockLlmProvider llamacpp = new MockLlamaCppProvider();

            assertEquals("openai", openai.getProviderName());
            assertEquals("ollama", ollama.getProviderName());
            assertEquals("llamacpp", llamacpp.getProviderName());
        }

        @Test
        @DisplayName("should generate different responses"
            + " for different providers")
        void testProviderResponses() {
            String prompt = "generate code";

            String openaiResponse = new MockOpenAiProvider().complete(prompt);
            String ollamaResponse = new MockOllamaProvider().complete(prompt);
            String llamacppResponse =
                new MockLlamaCppProvider().complete(prompt);

            assertTrue(openaiResponse.contains("Mock OpenAI"));
            assertTrue(ollamaResponse.contains("Mock Ollama"));
            assertTrue(llamacppResponse.contains("Mock llama.cpp"));
        }

        @Test
        @DisplayName("should handle null messages in chat")
        void testNullMessagesInChat() {
            MockLlmProvider provider = new MockOpenAiProvider();
            String response = provider.chat(null);

            assertNotNull(response);
            assertTrue(response.contains("Empty message"));
        }

        @Test
        @DisplayName("should handle empty message list")
        void testEmptyMessageList() {
            MockLlmProvider provider = new MockOllamaProvider();
            String response = provider.chat(List.of());

            assertNotNull(response);
            assertTrue(response.contains("Empty message"));
        }
    }
}
