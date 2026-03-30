package com.example.demo.services;

import com.example.demo.config.OllamaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class OllamaAiService implements AiService {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public OllamaAiService(OllamaProperties properties) {
        this.restTemplate = new RestTemplate();
        String baseUrl = Objects.requireNonNull(properties.getApiUrl(), "Ollama API URL must not be null");
        this.apiUrl = baseUrl.endsWith("/") ? baseUrl + "api/generate" : baseUrl + "/api/generate";
    }

    @Override
    public String generateExplication(double ratio, double montant) {
        String prompt = buildPrompt(ratio, montant);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama3");
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 300);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);
            String url = Objects.requireNonNull(apiUrl, "apiUrl must not be null");
            HttpMethod method = HttpMethod.POST;
            Objects.requireNonNull(method, "HTTP method must not be null");
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    method,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );
            Map<String, Object> response = responseEntity.getBody();
            return extractTextResponse(response);
        } catch (RestClientException e) {
            log.error("Erreur lors de l'appel à Ollama : {}", e.getMessage(), e);
            return "Impossible de générer une explication IA pour le moment.";
        }
    }

    private String buildPrompt(double ratio, double montant) {
        String statut = ratio > 0.40 ? "refusé" : "accepté";
        return String.format(
                "Explique en français pourquoi un prêt de %.2f euros est %s pour un client avec un ratio d'endettement de %.2f. " +
                        "Précise les conséquences pour le dossier et propose des recommandations claires.",
                montant, statut, ratio
        );
    }

    private String extractTextResponse(Map<String, Object> response) {
        if (response == null) {
            return "Aucune réponse reçue de l'IA.";
        }

        Object results = response.get("results");
        if (results instanceof List<?> resultList && !resultList.isEmpty()) {
            Object firstResult = resultList.get(0);
            if (firstResult instanceof Map<?, ?> firstResultMap) {
                Object content = firstResultMap.get("content");
                if (content instanceof List<?> contentList && !contentList.isEmpty()) {
                    Object firstContent = contentList.get(0);
                    if (firstContent instanceof Map<?, ?> firstContentMap) {
                        Object text = firstContentMap.get("text");
                        if (text != null) {
                            return text.toString();
                        }
                    }
                }
            }
        }

        Object output = response.get("output");
        if (output != null) {
            return output.toString();
        }

        return "Réponse IA non structurée reçue.";
    }
}
