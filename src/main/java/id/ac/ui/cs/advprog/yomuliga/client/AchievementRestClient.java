package id.ac.ui.cs.advprog.yomuliga.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
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
@Primary
public class AchievementRestClient implements AchievementClient {

    private static final Logger log = LoggerFactory.getLogger(AchievementRestClient.class);
    private static final double FALLBACK_COMPLETION_RATE = 0.5;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String dailyMissionPath;

    public AchievementRestClient(
            ObjectMapper objectMapper,
            @Value("${achievement.service.base-url:http://localhost:8085}") String baseUrl,
            @Value("${achievement.service.daily-mission-path:/api/internal/league/achievements/clans/{clanId}/daily-mission-completion-percentage}") String dailyMissionPath
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(requestFactory);
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.dailyMissionPath = dailyMissionPath;
    }

    @Override
    public double getDailyMissionCompletionPercentage(String clanId) {
        if (clanId == null || clanId.trim().isEmpty()) {
            return FALLBACK_COMPLETION_RATE;
        }

        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path(dailyMissionPath)
                    .buildAndExpand(clanId)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Achievement service returned non-2xx status {} for clanId={}", response.getStatusCode(), clanId);
                return FALLBACK_COMPLETION_RATE;
            }

            return parseCompletionRate(response.getBody());
        } catch (RestClientException | IllegalArgumentException ex) {
            log.warn("Failed to fetch achievement completion rate for clanId={}: {}", clanId, ex.getMessage());
            return FALLBACK_COMPLETION_RATE;
        }
    }

    private double parseCompletionRate(String body) {
        if (body == null || body.trim().isEmpty()) {
            return FALLBACK_COMPLETION_RATE;
        }

        String trimmed = body.trim();

        try {
            return clamp(Double.parseDouble(trimmed));
        } catch (NumberFormatException ignored) {
            // Continue to JSON parsing.
        }

        try {
            JsonNode root = objectMapper.readTree(trimmed);
            JsonNode valueNode = extractNumericNode(root);

            if (valueNode != null) {
                if (valueNode.isNumber()) {
                    return clamp(valueNode.doubleValue());
                }
                if (valueNode.isTextual()) {
                    return clamp(Double.parseDouble(valueNode.asText().trim()));
                }
            }
        } catch (Exception ex) {
            log.warn("Unable to parse achievement response body: {}", ex.getMessage());
        }

        return FALLBACK_COMPLETION_RATE;
    }

    private JsonNode extractNumericNode(JsonNode root) {
        if (root == null || root.isNull()) {
            return null;
        }

        if (root.isNumber() || root.isTextual()) {
            return root;
        }

        if (root.isObject()) {
            for (String key : new String[]{"completionRate", "percentage", "accuracy", "value"}) {
                JsonNode candidate = root.get(key);
                if (candidate != null) {
                    if (candidate.isNumber() || candidate.isTextual()) {
                        return candidate;
                    }
                    JsonNode nested = extractNumericNode(candidate);
                    if (nested != null) {
                        return nested;
                    }
                }
            }

            JsonNode dataNode = root.get("data");
            if (dataNode != null) {
                JsonNode nested = extractNumericNode(dataNode);
                if (nested != null) {
                    return nested;
                }
            }
        }

        if (root.isArray() && !root.isEmpty()) {
            for (JsonNode element : root) {
                JsonNode nested = extractNumericNode(element);
                if (nested != null) {
                    return nested;
                }
            }
        }

        return null;
    }

    private double clamp(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return FALLBACK_COMPLETION_RATE;
        }

        if (value < 0.0) {
            return 0.0;
        }

        return Math.min(value, 1.0);
    }
}


