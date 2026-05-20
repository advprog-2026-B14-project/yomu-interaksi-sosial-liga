package id.ac.ui.cs.advprog.yomuliga.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LearningRestClient implements LearningClient {

    private static final Logger log = LoggerFactory.getLogger(LearningRestClient.class);
    private static final double FALLBACK_ACCURACY = 100.0;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String statsPath;
    private final String serviceToken;

    public LearningRestClient(ObjectMapper objectMapper,
                              @Value("${learning.service.base-url}") String baseUrl,
                              @Value("${learning.service.stats-path:/api/internal/league/statistics/students/{studentId}}") String statsPath,
                              @Value("${learning.service.token}") String serviceToken) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(requestFactory);
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.statsPath = statsPath;
        this.serviceToken = serviceToken;
    }

    @Override
    public double getStudentAccuracy(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return FALLBACK_ACCURACY;
        }

        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path(statsPath)
                    .buildAndExpand(studentId)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            headers.set("X-Internal-Service-Token", serviceToken);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Learning service returned non-2xx status {} for studentId={}", response.getStatusCode(), studentId);
                return FALLBACK_ACCURACY;
            }

            return parseAccuracy(response.getBody());
        } catch (RestClientException | IllegalArgumentException ex) {
            log.warn("Failed to fetch student accuracy for studentId={}: {}", studentId, ex.getMessage());
            return FALLBACK_ACCURACY;
        }
    }

    private double parseAccuracy(String body) {
        if (body == null || body.trim().isEmpty()) {
            return FALLBACK_ACCURACY;
        }

        String trimmed = body.trim();

        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
            // continue to json parsing
        }

        try {
            JsonNode root = objectMapper.readTree(trimmed);
            JsonNode valueNode = extractNumericNode(root);
            if (valueNode != null) {
                if (valueNode.isNumber()) {
                    return valueNode.doubleValue();
                }
                if (valueNode.isTextual()) {
                    return Double.parseDouble(valueNode.asText().trim());
                }
            }
        } catch (Exception ex) {
            log.warn("Unable to parse learning response body: {}", ex.getMessage());
        }

        return FALLBACK_ACCURACY;
    }

    private JsonNode extractNumericNode(JsonNode root) {
        if (root == null || root.isNull()) {
            return null;
        }

        if (root.isNumber() || root.isTextual()) {
            return root;
        }

        if (root.isObject()) {
            for (String key : new String[]{"accuracy", "accuracyPercentage", "accuracy_rate", "accuracy_percentage", "value", "data", "percentage"}) {
                JsonNode candidate = root.get(key);
                if (candidate != null) {
                    if (candidate.isNumber() || candidate.isTextual()) {
                        return candidate;
                    }
                    JsonNode nested = extractNumericNode(candidate);
                    if (nested != null) return nested;
                }
            }

            JsonNode dataNode = root.get("data");
            if (dataNode != null) {
                JsonNode nested = extractNumericNode(dataNode);
                if (nested != null) return nested;
            }
        }

        if (root.isArray() && !root.isEmpty()) {
            for (JsonNode element : root) {
                JsonNode nested = extractNumericNode(element);
                if (nested != null) return nested;
            }
        }

        return null;
    }
}

