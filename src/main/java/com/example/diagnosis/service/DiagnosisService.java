package com.example.diagnosis.service;

import com.example.diagnosis.model.DiagnosisResult;
import com.example.diagnosis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DiagnosisService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosisService.class);

    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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";

    // ✅ Prioritized free models (as of July 2025)
    private static final List<String> MODEL_PRIORITY_LIST = Arrays.asList(
        "mistralai/mistral-7b-instruct:free",
        "meta-llama/llama-3-8b-instruct:free",
        "google/gemma-7b-it:free",
        "openchat/openchat-3.5:free",
        "gryphe/mythomax-l2-13b:free",
        "undi95/toppy-m-7b:free",
        "open-orca/mistral-7b-openorca:free",
        "nousresearch/nous-capybara-7b:free"
    );

    private final RestTemplate restTemplate = new RestTemplate();

    public DiagnosisResult performDiagnosis(Patient patient) {
        String systemPrompt = buildSystemPrompt();
        String userMessage = buildUserMessage(patient);

        for (String model : MODEL_PRIORITY_LIST) {
            try {
                logger.info("🧪 Trying model: {}", model);
                JSONObject payload = createRequestPayload(systemPrompt, userMessage, model);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(API_KEY);
                headers.set("X-Title", "Free Diagnosis");

                HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);
                ResponseEntity<String> response = restTemplate.postForEntity(OPENROUTER_API_URL, request, String.class);

                logger.info("📥 Response status: {} from model: {}", response.getStatusCode(), model);

                if (response.getStatusCode() == HttpStatus.OK) {
                    DiagnosisResult result = parseResponse(response.getBody());
                    if (result != null && !"Unable to diagnose".equalsIgnoreCase(result.getDisease())) {
                        logger.info("✅ Diagnosis successful with model: {}", model);
                        return result;
                    } else {
                        logger.warn("⚠️ Model {} gave incomplete or invalid result.", model);
                    }
                } else {
                    logger.warn("❌ Model {} returned status: {}", model, response.getStatusCode());
                }

            } catch (HttpClientErrorException e) {
                logger.warn("⚠️ Model {} failed with HTTP {}: {}", model, e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    continue; // Try next model
                }
            } catch (Exception e) {
                logger.error("💥 Model {} threw exception: {}", model, e.getMessage(), e);
            }
        }

        logger.error("🚫 All models failed. Returning fallback response.");
        return createErrorResult("All models failed or rate-limited. Try again shortly.");
    }

    private String buildSystemPrompt() {
        return "You are a highly knowledgeable medical AI assistant. Based on patient information provided, "
            + "return a structured JSON object like this:\n"
            + "{\n"
            + "  \"disease\": \"Most likely condition\",\n"
            + "  \"cause\": \"Cause of the condition\",\n"
            + "  \"cure\": \"Treatment plan\",\n"
            + "  \"medicines\": [\"Medicine1\", \"Medicine2\"],\n"
            + "  \"severity\": \"Mild/Moderate/Severe\",\n"
            + "  \"recommendations\": \"Care advice\",\n"
            + "  \"precautions\": \"Precautionary steps\",\n"
            + "  \"requiresImmediateAttention\": true/false\n"
            + "}\nRespond ONLY with this JSON structure.";
    }

    private String buildUserMessage(Patient patient) {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient Information:\n")
            .append("Name: ").append(patient.getName()).append("\n")
            .append("Age: ").append(patient.getAge()).append(" years\n")
            .append("Weight: ").append(patient.getWeight()).append(" kg\n")
            .append("Blood Pressure: ").append(patient.getBloodPressure()).append("\n")
            .append("Pulse: ").append(patient.getPulse()).append(" bpm\n")
            .append("Temperature: ").append(patient.getTemperature()).append(" °F\n")
            .append("Respiration Rate: ").append(patient.getRespirationRate()).append(" breaths/min\n")
            .append("Symptoms: ").append(patient.getSymptoms()).append("\n");

        if (patient.getGender() != null)
            sb.append("Gender: ").append(patient.getGender()).append("\n");
        if (patient.getMedicalHistory() != null)
            sb.append("Medical History: ").append(patient.getMedicalHistory()).append("\n");
        if (patient.getAllergies() != null)
            sb.append("Allergies: ").append(patient.getAllergies()).append("\n");
        if (patient.getCurrentMedications() != null)
            sb.append("Current Medications: ").append(patient.getCurrentMedications()).append("\n");

        sb.append("\nReturn diagnosis using the JSON structure above.");
        return sb.toString();
    }

    private JSONObject createRequestPayload(String systemPrompt, String userMessage, String model) {
        JSONObject payload = new JSONObject();
        payload.put("model", model);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
        messages.put(new JSONObject().put("role", "user").put("content", userMessage));

        payload.put("messages", messages);
        payload.put("max_tokens", 1200);
        payload.put("temperature", 0.5);
        return payload;
    }

    private DiagnosisResult parseResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray choices = json.optJSONArray("choices");
            if (choices == null || choices.isEmpty()) return null;

            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
            String jsonText = extractJsonFromResponse(content);
            if (jsonText != null) {
                return mapJsonToDiagnosisResult(new JSONObject(jsonText));
            }
        } catch (Exception e) {
            logger.error("❌ Failed to parse OpenRouter response: {}", e.getMessage(), e);
        }
        return null;
    }

    private String extractJsonFromResponse(String content) {
        Pattern jsonPattern = Pattern.compile("\\{(?:[^{}]|\\{[^{}]*\\})*\\}", Pattern.DOTALL);
        Matcher matcher = jsonPattern.matcher(content);
        return matcher.find() ? matcher.group() : null;
    }

    private DiagnosisResult mapJsonToDiagnosisResult(JSONObject json) {
        DiagnosisResult result = new DiagnosisResult();
        result.setDisease(json.optString("disease", "Unknown"));
        result.setCause(json.optString("cause", "N/A"));
        result.setCure(json.optString("cure", "N/A"));

        JSONArray medsArray = json.optJSONArray("medicines");
        List<String> meds = new ArrayList<>();
        if (medsArray != null) {
            for (int i = 0; i < medsArray.length(); i++) {
                meds.add(medsArray.getString(i));
            }
        }
        result.setMedicines(meds);

        result.setSeverity(json.optString("severity", "Unknown"));
        result.setRecommendations(json.optString("recommendations", ""));
        result.setPrecautions(json.optString("precautions", ""));
        result.setRequiresImmediateAttention(json.optBoolean("requiresImmediateAttention", false));
        return result;
    }

    private DiagnosisResult createErrorResult(String message) {
        DiagnosisResult result = new DiagnosisResult();
        result.setDisease("Unable to diagnose");
        result.setCause("Reason: " + message);
        result.setCure("Consult a healthcare professional.");
        result.setMedicines(Collections.singletonList("Consult doctor"));
        result.setSeverity("Unknown");
        result.setRecommendations("Visit a clinic or hospital.");
        result.setPrecautions("Do not delay care.");
        result.setRequiresImmediateAttention(true);
        return result;
    }
}
