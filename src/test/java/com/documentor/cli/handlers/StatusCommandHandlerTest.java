package com.documentor.cli.handlers;

import com.documentor.config.DocumentorConfig;
import com.documentor.config.model.AnalysisSettings;
import com.documentor.config.model.LlmModelConfig;
import com.documentor.config.model.OutputSettings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusCommandHandlerTest {

    @Test
    void showStatusIncludesProjectAndConfigInfo() {
        LlmModelConfig model = new LlmModelConfig("m", "openai", "http://x", "apikey123456", 200, 20);
        OutputSettings output = new OutputSettings("out", "md", true, true);
        AnalysisSettings analysis = new AnalysisSettings(true, 2, List.of("**/*.java"), List.of("**/test/**"));

        DocumentorConfig cfg = new DocumentorConfig(List.of(model), output, analysis);
        StatusCommandHandler handler = new StatusCommandHandler(cfg);

        String status = handler.handleShowStatus(null, null);
        assertTrue(status.contains("Documentor Status"));
        assertTrue(status.contains("LLM Models") || status.contains("No LLM models"));
    }

    @Test
    void showInfoAndQuickStartReturnNonEmpty() {
        StatusCommandHandler handler = new StatusCommandHandler(null);

        String info = handler.handleShowInfo();
        String quick = handler.handleQuickStart();

        assertTrue(info.contains("Supported File Types"));
        assertTrue(quick.contains("Quick Start Guide"));
    }
}
