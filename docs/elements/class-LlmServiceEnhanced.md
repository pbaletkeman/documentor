# 📦 LlmServiceEnhanced

> **Package:** `com.documentor.service`

---

## 📄 Class Documentation

Error: LLM configuration is null. Please check the application configuration.

---

## 💡 Class Usage Examples

Error: LLM configuration is null. Please check the application configuration.

---

## 📋 Class Signature

```java
/** * LLM Integration Service - Enhanced with improved error handling and null * safety */ public class LlmServiceEnhanced {
   private static final Logger LOGGER = LoggerFactory.getLogger(LlmServiceEnhanced.class);
 private final DocumentorConfig config;
 private final LlmRequestBuilder requestBuilder;
 private final LlmResponseHandler responseHandler;
 private final LlmApiClient apiClient;
 /** * Thread-local executor for CompletableFuture tasks to ensure config * propagation */ private final Executor threadLocalExecutor;
 /** * Fallback executor when threadLocalExecutor is unavailable */ private static final Executor FALLBACK_EXECUTOR = ForkJoinPool.commonPool();
 public LlmServiceEnhanced(final DocumentorConfig configParam, final LlmRequestBuilder requestBuilderParam, final LlmResponseHandler responseHandlerParam, final LlmApiClient apiClientParam) {
   this.config = configParam;
 this.requestBuilder = requestBuilderParam;
 this.responseHandler = responseHandlerParam;
 this.apiClient = apiClientParam;
 // Store config in ThreadLocal when service is created if (configParam != null) {
   ThreadLocalContextHolder.setConfig(configParam);
 LOGGER.debug("Config stored in ThreadLocalContextHolder during " + "LlmServiceEnhanced initialization");
 } // Create the thread-local propagating executor with safe error handling Executor safeExecutor = null;
 try {
   safeExecutor = ThreadLocalPropagatingExecutorEnhanced.createExecutor(getWorkerThreadCount(), "llm-worker-enhanced");
 LOGGER.info("ThreadLocalPropagatingExecutorEnhanced created " + "successfully");
 } catch (Exception e) {
   LOGGER.error("Failed to create " + "ThreadLocalPropagatingExecutorEnhanced: {}", e.getMessage());
 // We'll use the fallback executor later if this fails } this.threadLocalExecutor = safeExecutor;
 } /** * Get the ThreadLocal config - used by ThreadLocalTaskDecorator */ public static DocumentorConfig getThreadLocalConfig() {
   return ThreadLocalContextHolder.getConfig();
 } /** * Set the ThreadLocal config - used by ThreadLocalTaskDecorator */ public static void setThreadLocalConfig(final DocumentorConfig config) {
   ThreadLocalContextHolder.setConfig(config);
 } /** * Clear the ThreadLocal config - used by ThreadLocalTaskDecorator to * prevent memory leaks */ public static void clearThreadLocalConfig() {
   ThreadLocalContextHolder.clearConfig();
 } /** * Gets the executor to use for CompletableFuture operations * - Uses the ThreadLocalPropagatingExecutor if available * - Falls back to common pool if ThreadLocalPropagatingExecutor is null * * @return An executor that's guaranteed not to be null */ private Executor getExecutor() {
   if (threadLocalExecutor != null) {
   return threadLocalExecutor;
 } LOGGER.warn("Using fallback executor (ForkJoinPool.commonPool) - " + "ThreadLocal values may not propagate correctly");
 return FALLBACK_EXECUTOR;
 } /** * Generate documentation for a code element using the default LLM model */ @Async("llmExecutor") public final CompletableFuture<String> generateDocumentation(final CodeElement codeElement) {
   LOGGER.info("Generating documentation for: {}", codeElement.getDisplayName());
 // Store config in ThreadLocal to ensure it's available in this // async context if (config != null) {
   setThreadLocalConfig(config);
 } // Try to get config from ThreadLocal if it's null DocumentorConfig effectiveConfig = config != null ? config : getThreadLocalConfig();
 if (effectiveConfig == null) {
   LOGGER.error("Configuration is null in " + "LlmServiceEnhanced.generateDocumentation");
 return CompletableFuture.completedFuture("Error: LLM configuration is null. Please check the " + "application configuration.");
 } if (effectiveConfig.llmModels().isEmpty()) {
   return CompletableFuture.completedFuture("No LLM models configured for documentation generation.");
 } final LlmModelConfig model = effectiveConfig.llmModels().get(0);
 LOGGER.info("Using LLM model: {}", model.name());
 try {
   // Use our ThreadLocalPropagatingExecutor with safe fallback to // ensure config is available return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "documentation"), getExecutor());
 } catch (NullPointerException e) {
   LOGGER.error("NullPointerException in CompletableFuture for " + "documentation generation: {}", e.getMessage());
 // Additional logging for diagnostic purposes LOGGER.error("Documentation generation falling back to synchronous " + "execution due to: ", e);
 // Synchronous fallback - directly call the method String result = generateWithModel(codeElement, model, "documentation");
 return CompletableFuture.completedFuture(result);
 } } /** * Generate usage examples for a code element using the default LLM model */ @Async("llmExecutor") public final CompletableFuture<String> generateUsageExamples(final CodeElement codeElement) {
   LOGGER.info("Generating usage examples for: {}", codeElement.getDisplayName());
 // Store config in ThreadLocal to ensure it's available if (config != null) {
   setThreadLocalConfig(config);
 } // Try to get config from ThreadLocal if it's null DocumentorConfig effectiveConfig = config != null ? config : getThreadLocalConfig();
 if (effectiveConfig == null) {
   LOGGER.error("Configuration is null in " + "LlmServiceEnhanced.generateUsageExamples");
 return CompletableFuture.completedFuture("Error: LLM configuration is null. Please check the " + "application configuration.");
 } if (effectiveConfig.llmModels().isEmpty()) {
   return CompletableFuture.completedFuture("No LLM models configured for example generation.");
 } final LlmModelConfig model = effectiveConfig.llmModels().get(0);
 LOGGER.info("Using LLM model for examples: {}", model.name());
 try {
   // Use our ThreadLocalPropagatingExecutor with safe fallback to // ensure config is available return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "usage"), getExecutor());
 } catch (NullPointerException e) {
   LOGGER.error("NullPointerException in CompletableFuture for usage " + "examples: {}", e.getMessage());
 // Additional logging for diagnostic purposes LOGGER.error("Usage examples generation falling back to " + "synchronous execution due to: ", e);
 // Synchronous fallback - directly call the method String result = generateWithModel(codeElement, model, "usage");
 return CompletableFuture.completedFuture(result);
 } } @Async("llmExecutor") public final CompletableFuture<String> generateUnitTests(final CodeElement codeElement) {
   LOGGER.info("Generating unit tests for: {}", codeElement.getDisplayName());
 // Store config in ThreadLocal to ensure it's available if (config != null) {
   setThreadLocalConfig(config);
 } // Try to get config from ThreadLocal if it's null DocumentorConfig effectiveConfig = config != null ? config : getThreadLocalConfig();
 if (effectiveConfig == null) {
   LOGGER.error("Configuration is null in " + "LlmServiceEnhanced.generateUnitTests");
 return CompletableFuture.completedFuture("Error: LLM configuration is null. Please check the " + "application configuration.");
 } if (effectiveConfig.llmModels().isEmpty()) {
   return CompletableFuture.completedFuture("No LLM models configured for unit test generation.");
 } final LlmModelConfig model = effectiveConfig.llmModels().get(0);
 LOGGER.info("Using LLM model for unit tests: {}", model.name());
 try {
   // Use our ThreadLocalPropagatingExecutor with safe fallback to // ensure config is available return CompletableFuture.supplyAsync(() -> generateWithModel(codeElement, model, "tests"), getExecutor());
 } catch (NullPointerException e) {
   LOGGER.error("NullPointerException in CompletableFuture for unit " + "tests: {}", e.getMessage());
 // Additional logging for diagnostic purposes LOGGER.error("Unit test generation falling back to synchronous " + "execution due to: ", e);
 // Synchronous fallback - directly call the method String result = generateWithModel(codeElement, model, "tests");
 return CompletableFuture.completedFuture(result);
 } } /** * Gets the worker thread count. * * @return the worker thread count */ private int getWorkerThreadCount() {
   return ApplicationConstants.DEFAULT_WORKER_THREAD_COUNT;
 } /** * Generate content with the specified model */ private String generateWithModel(final CodeElement codeElement, final LlmModelConfig model, final String type) {
   try {
   // Diagnostic logging to verify that ThreadLocal config is available ThreadLocalContextHolder.logConfigStatus();
 String prompt = createPrompt(codeElement, type);
 Map<String, Object> requestBody = requestBuilder.buildRequestBody(model, prompt);
 String endpoint = responseHandler.getModelEndpoint(model);
 String response = apiClient.callLlmModel(model, endpoint, requestBody);
 return responseHandler.extractResponseContent(response, model);
 } catch (Exception e) {
   LOGGER.error("Error generating {} with model {}: {}", type, model.name(), e.getMessage());
 return "Error generating " + type + " with " + model.name() + ": " + e.getMessage();
 } } private String createPrompt(final CodeElement codeElement, final String type) {
   // Java 17: Traditional switch statement (Java 21 used switch expressions) switch(type) {
   case "documentation": return requestBuilder.createDocumentationPrompt(codeElement);
 case "usage": return requestBuilder.createUsageExamplePrompt(codeElement);
 case "tests": return requestBuilder.createUnitTestPrompt(codeElement);
 default: return requestBuilder.createDocumentationPrompt(codeElement);
 } } }
```

