package com.jarurat.jarurat_care.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarurat.jarurat_care.model.RegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RegistrationController {

    // ✅ Key lives in application.properties — never in frontend code
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private static final String SYSTEM_PROMPT =
            "You are a compassionate AI assistant for Jarurat Care, India's largest cancer care community NGO.\n\n" +
                    "Your role:\n" +
                    "- Answer questions about cancer support, caregiving, volunteer opportunities, and the organization\n" +
                    "- Provide emotional support and resources for cancer patients and families\n" +
                    "- Explain how to register for support or volunteer\n" +
                    "- Share information about preventive care and awareness\n\n" +
                    "Key facts about Jarurat Care:\n" +
                    "- Mission: Build the largest cancer care community in India\n" +
                    "- Services: Awareness campaigns, caregiver mentorship, support networks\n" +
                    "- Coverage: 28 cities across India, 12,000+ families supported, 800+ volunteers\n" +
                    "- Registration: Free of cost, response within 24 hours\n" +
                    "- Focus: Rare cancers awareness, caregiver burnout prevention, community building\n\n" +
                    "Always be warm, empathetic, and encouraging. Keep responses concise (2-4 sentences).\n" +
                    "If someone seems distressed, acknowledge their feelings and guide them to register for support.\n" +
                    "Do not provide specific medical advice — always recommend consulting a doctor for medical questions.";

    // In-memory store (no DB needed for assignment)
    private final List<RegistrationRequest> registrations = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ===== HEALTH CHECK =====
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Jarurat Care API");
        return ResponseEntity.ok(response);
    }

    // ===== REGISTRATION =====
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegistrationRequest request) {
        registrations.add(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Thank you " + request.getName() + "! We have received your " +
                (request.getType().equals("volunteer") ? "volunteer application" : "support request") +
                ". Our team will contact you within 24 hours.");
        response.put("registrationId", "JC-" + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<RegistrationRequest>> getAllRegistrations() {
        return ResponseEntity.ok(registrations);
    }

    // ===== CHAT PROXY — keeps Gemini key safe on server =====
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Build Gemini request payload
            Map<String, Object> geminiPayload = new HashMap<>();

            // System instruction
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));
            geminiPayload.put("system_instruction", systemInstruction);

            // Pass through history from frontend (already trimmed to last 10)
            geminiPayload.put("contents", body.get("history"));

            // HTTP call to Gemini
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(geminiPayload, headers);

            ResponseEntity<String> geminiResponse = restTemplate.postForEntity(
                    GEMINI_URL + geminiApiKey,
                    entity,
                    String.class
            );

            JsonNode json = objectMapper.readTree(geminiResponse.getBody());
            String reply = json.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("I'm sorry, I couldn't process that. Please try again.");

            result.put("reply", reply);
            return ResponseEntity.ok(result);

        }catch (Exception e) {
        System.out.println("CHAT ERROR: " + e.getMessage()); // ADD THIS
        result.put("error", "Chat service unavailable. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    }
}