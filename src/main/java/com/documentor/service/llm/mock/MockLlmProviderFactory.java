package com.documentor.service.llm.mock;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 🏭 MockLlmProviderFactory - Factory for creating mock LLM provider instances
 *
 * Centralizes creation of mock LLM providers with caching and registration.
 * Supports OpenAI, Ollama, and llama.cpp providers.
 *
 * Usage:
 * <pre>{@code
 * MockLlmProvider provider = MockLlmProviderFactory.createProvider("openai");
 * String response = provider.complete("Generate code");
 *
 * // Or use factory to get cached provider
 * MockLlmProvider cached = MockLlmProviderFactory.getProvider("ollama");
 * }</pre>
 */
public final class MockLlmProviderFactory {
    private static final Logger logger = LoggerFactory.getLogger(MockLlmProviderFactory.class);

    private static final Map<String, MockLlmProvider> providerCache = new HashMap<>();

    /**
     * Supported provider types.
     */
    public enum ProviderType {
        OPENAI("openai"),
        OLLAMA("ollama"),
        LLAMACPP("llamacpp");

        private final String id;

        ProviderType(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        /**
         * Gets provider type by ID string.
         *
         * @param id provider ID
         * @return provider type or null if not found
         */
        public static ProviderType fromId(final String id) {
            if (id == null) {
                return null;
            }
            for (final ProviderType type : ProviderType.values()) {
                if (type.id.equalsIgnoreCase(id)) {
                    return type;
                }
            }
            return null;
        }
    }

    private MockLlmProviderFactory() {
        // Private constructor - utility class
    }

    /**
     * Creates a new mock provider instance without caching.
     *
     * @param providerType the provider type to create
     * @return new mock provider instance
     * @throws IllegalArgumentException if provider type is unsupported
     */
    public static MockLlmProvider createProvider(final ProviderType providerType) {
        return createProvider(providerType, null);
    }

    /**
     * Creates a new mock provider instance with specified model.
     *
     * @param providerType the provider type to create
     * @param model the model name to use (optional)
     * @return new mock provider instance
     * @throws IllegalArgumentException if provider type is unsupported
     */
    public static MockLlmProvider createProvider(final ProviderType providerType,
            final String model) {
        if (providerType == null) {
            throw new IllegalArgumentException("Provider type cannot be null");
        }

        return switch (providerType) {
            case OPENAI -> {
                logger.debug("Creating MockOpenAiProvider with model: {}", model);
                yield new MockOpenAiProvider(model);
            }
            case OLLAMA -> {
                logger.debug("Creating MockOllamaProvider with model: {}", model);
                yield new MockOllamaProvider(model);
            }
            case LLAMACPP -> {
                logger.debug("Creating MockLlamaCppProvider with model: {}", model);
                yield new MockLlamaCppProvider(model);
            }
        };
    }

    /**
     * Creates a mock provider from string provider name.
     *
     * @param providerName the provider name (e.g., "openai", "ollama", "llamacpp")
     * @return new mock provider instance
     * @throws IllegalArgumentException if provider name is not recognized
     */
    public static MockLlmProvider createProvider(final String providerName) {
        return createProvider(providerName, null);
    }

    /**
     * Creates a mock provider from string provider name with model.
     *
     * @param providerName the provider name
     * @param model the model name to use (optional)
     * @return new mock provider instance
     * @throws IllegalArgumentException if provider name is not recognized
     */
    public static MockLlmProvider createProvider(final String providerName,
            final String model) {
        if (providerName == null || providerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider name cannot be null or empty");
        }

        ProviderType type = ProviderType.fromId(providerName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown provider: " + providerName);
        }

        return createProvider(type, model);
    }

    /**
     * Gets or creates a cached mock provider instance.
     *
     * @param providerType the provider type
     * @return cached or new mock provider instance
     */
    public static MockLlmProvider getProvider(final ProviderType providerType) {
        return getProvider(providerType, null);
    }

    /**
     * Gets or creates a cached mock provider with specific model.
     *
     * @param providerType the provider type
     * @param model the model name to use (optional)
     * @return cached or new mock provider instance
     */
    public static MockLlmProvider getProvider(final ProviderType providerType,
            final String model) {
        if (providerType == null) {
            throw new IllegalArgumentException("Provider type cannot be null");
        }

        String cacheKey = providerType.getId();
        if (model != null && !model.trim().isEmpty()) {
            cacheKey = cacheKey + ":" + model;
        }

        return providerCache.computeIfAbsent(cacheKey, key -> {
            logger.debug("Creating and caching provider: {}", key);
            return createProvider(providerType, model);
        });
    }

    /**
     * Gets or creates a cached mock provider from string name.
     *
     * @param providerName the provider name
     * @return cached or new mock provider instance
     */
    public static MockLlmProvider getProvider(final String providerName) {
        return getProvider(providerName, null);
    }

    /**
     * Gets or creates a cached mock provider from string name and model.
     *
     * @param providerName the provider name
     * @param model the model name to use (optional)
     * @return cached or new mock provider instance
     */
    public static MockLlmProvider getProvider(final String providerName,
            final String model) {
        ProviderType type = ProviderType.fromId(providerName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown provider: " + providerName);
        }
        return getProvider(type, model);
    }

    /**
     * Clears all cached providers.
     */
    public static void clearCache() {
        logger.debug("Clearing provider cache");
        providerCache.clear();
    }

    /**
     * Removes a specific provider from cache.
     *
     * @param providerType the provider type to remove
     */
    public static void removeFromCache(final ProviderType providerType) {
        removeFromCache(providerType, null);
    }

    /**
     * Removes a specific provider with model from cache.
     *
     * @param providerType the provider type to remove
     * @param model the model to remove (optional)
     */
    public static void removeFromCache(final ProviderType providerType, final String model) {
        String cacheKey = providerType.getId();
        if (model != null && !model.trim().isEmpty()) {
            cacheKey = cacheKey + ":" + model;
        }
        logger.debug("Removing provider from cache: {}", cacheKey);
        providerCache.remove(cacheKey);
    }

    /**
     * Gets the number of cached providers.
     *
     * @return cache size
     */
    public static int getCacheSize() {
        return providerCache.size();
    }
}
