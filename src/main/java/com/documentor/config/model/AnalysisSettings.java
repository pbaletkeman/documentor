package com.documentor.config.model;

import com.documentor.constants.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * ðŸ” Analysis Settings Configuration - Simplified
 */
public record AnalysisSettings(
    @JsonProperty("include_private_members")
    Boolean includePrivateMembers,

    @JsonProperty("max_depth")
    Integer maxDepth,

    @JsonProperty("included_patterns")
    List<String> includedPatterns,

    @JsonProperty("excluded_patterns")
    List<String> excludePatterns
) {
    // Simplified defaults
    public AnalysisSettings {
        if (includePrivateMembers == null) {
            includePrivateMembers = false;
        }
        if (maxDepth == null) {
            maxDepth = ApplicationConstants.DEFAULT_MAX_DEPTH;
        }
        if (includedPatterns == null) {
            includedPatterns = List.of("**/*.java", "**/*.py");
        }
        if (excludePatterns == null) {
            excludePatterns = List.of("**/test/**", "**/target/**");
        }
    }

    // Backward compatibility methods
    public Integer maxThreads() {
        // If a maxDepth is provided in configuration, interpret it as the desired
        // number of worker threads. The executor will then set max pool size as
        // core * DEFAULT_THREAD_MULTIPLIER in AppConfig. If not provided fall back
        // to the available processors.
        if (maxDepth != null && maxDepth > 0) {
            return Math.max(1, maxDepth);
        }
        return Runtime.getRuntime().availableProcessors();
    }

    public List<String> supportedLanguages() {
        return List.of("java", "python");
    }
}
