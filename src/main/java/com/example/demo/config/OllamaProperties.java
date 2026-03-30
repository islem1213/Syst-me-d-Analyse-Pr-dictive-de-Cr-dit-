package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ollama")
public class OllamaProperties {

    /**
     * Base URL for the Ollama/Llama 3 API.
     */
    private String apiUrl = "http://localhost:11434";
}
