package com.documentor.config.model;

import com.documentor.constants.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 🔍 Analysis Settings Configuration - Simplified
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
        return Runtime.getRuntime().availableProcessors();
    }

    public List<String> supportedLanguages() {
        return List.of("java", "python");
    }
}