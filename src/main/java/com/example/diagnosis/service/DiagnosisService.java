package com.example.diagnosis.service;

import com.example.diagnosis.model.DiagnosisResult;
import com.example.diagnosis.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DiagnosisService {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosisService.class);
    @Value("${groq.api.key}")
    private String groqApiKey;


    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama3-70b-8192";

    private final RestTemplate restTemplate = new RestTemplate();

    public DiagnosisResult performDiagnosis(Patient patient) {
        String systemPrompt = buildSystemPrompt();
        String userMessage = buildUserMessage(patient);

        try {
            logger.info("🧠 Using model: {}", MODEL);
            JSONObject payload = createRequestPayload(systemPrompt, userMessage);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Mozilla/5.0");
            headers.setBearerAuth(groqApiKey); // ✅ Uses Groq Bearer token

            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(GROQ_API_URL, request, String.class);

            logger.info("📥 Response status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                DiagnosisResult result = parseResponse(response.getBody());
                if (result != null && !"Unable to diagnose".equalsIgnoreCase(result.getDisease())) {
                    logger.info("✅ Diagnosis successful");
                    return result;
                } else {
                    logger.warn("⚠️ Incomplete or invalid diagnosis.");
                }
            } else {
                logger.warn("❌ Request failed with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("💥 Exception during diagnosis: {}", e.getMessage(), e);
        }

        return createErrorResult("Diagnosis failed. Try again later.");
    }

    private String buildSystemPrompt() {
        return "You are a medical diagnosis assistant. Based on the provided patient information, respond strictly in this JSON format:\n"
            + "{\n"
            + "  \"disease\": \"Name of likely disease\",\n"
            + "  \"cause\": \"Cause of the disease\",\n"
            + "  \"cure\": \"Suggested treatment\",\n"
            + "  \"medicines\": [\"Medicine1\", \"Medicine2\"],\n"
            + "  \"severity\": \"Mild/Moderate/Severe\",\n"
            + "  \"recommendations\": \"General recommendations\",\n"
            + "  \"precautions\": \"Precautions to take\",\n"
            + "  \"requiresImmediateAttention\": true/false\n"
            + "}\nReturn only the JSON response.";
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

        sb.append("\nRespond strictly in JSON format as described.");
        return sb.toString();
    }

    private JSONObject createRequestPayload(String systemPrompt, String userMessage) {
        JSONObject payload = new JSONObject();
        payload.put("model", MODEL);

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
            logger.error("❌ Failed to parse Groq response: {}", e.getMessage(), e);
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
