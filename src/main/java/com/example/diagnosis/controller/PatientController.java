package com.example.diagnosis.controller;

import com.example.diagnosis.dto.PatientDTO;
import com.example.diagnosis.model.DiagnosisResult;
import com.example.diagnosis.model.Patient;
import com.example.diagnosis.repository.PatientRepository;
import com.example.diagnosis.service.DiagnosisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private PatientRepository patientRepository;

    // Display the main patient input form
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("patient", new Patient());
        return "index";
    }

    // Handle the AJAX form submission for diagnosis
    @PostMapping("/diagnose")
    @ResponseBody
    public ResponseEntity<?> diagnose(@Valid @ModelAttribute Patient patient, BindingResult bindingResult) {
        try {
            logger.info("Received diagnosis request for patient: {}", patient.getName());

            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error -> 
                    errors.put(error.getField(), error.getDefaultMessage())
                );
                return ResponseEntity.badRequest().body(errors);
            }

            // Basic server-side validation
            if (patient.getName() == null || patient.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Patient name is required");
            }

            if (patient.getAge() == null || patient.getAge() < 0 || patient.getAge() > 150) {
                return ResponseEntity.badRequest().body("Valid age is required (0-150)");
            }

            if (patient.getWeight() == null || patient.getWeight() <= 0) {
                return ResponseEntity.badRequest().body("Valid weight is required");
            }

            if (patient.getBloodPressure() == null || patient.getBloodPressure().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Blood Pressure information is required");
            }
            if (patient.getPulse() == null) {
                return ResponseEntity.badRequest().body("Pulse information is required");
            }
            if (patient.getTemperature() == null) {
                return ResponseEntity.badRequest().body("Temperature information is required");
            }
            if (patient.getRespirationRate() == null) {
                return ResponseEntity.badRequest().body("Respiration Rate information is required");
            }

            if (patient.getSymptoms() == null || patient.getSymptoms().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Symptoms description is required");
            }

            // Perform the AI-powered diagnosis
            DiagnosisResult result = diagnosisService.performDiagnosis(patient);

            logger.info("Result {}",result);

            // Save the patient data for history (after successful diagnosis)
            try {
                patientRepository.save(patient);
                logger.info("Patient data saved successfully for: {}", patient.getName());
            } catch (Exception e) {
                logger.error("Error saving patient data: ", e);
                // Continue even if saving fails
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error during diagnosis process: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Diagnosis service is temporarily unavailable. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get patient history (additional feature)
    @GetMapping("/history")
    public String patientHistory(Model model) {
        try {
            List<Patient> recentPatients = patientRepository.findTop10ByOrderByCreatedAtDesc();
            List<PatientDTO> collect = recentPatients.stream().map(recentPatient -> {
                PatientDTO patientDTO = new PatientDTO();
                patientDTO.setId(recentPatient.getId());
                patientDTO.setName(recentPatient.getName());
                patientDTO.setAge(recentPatient.getAge());
                patientDTO.setWeight(recentPatient.getWeight());
                patientDTO.setBloodPressure(recentPatient.getBloodPressure());
                patientDTO.setPulse(recentPatient.getPulse());
                patientDTO.setTemperature(recentPatient.getTemperature());
                patientDTO.setRespirationRate(recentPatient.getRespirationRate());
                patientDTO.setSymptoms(recentPatient.getSymptoms());
                patientDTO.setGender(recentPatient.getGender());
                patientDTO.setMedicalHistory(recentPatient.getMedicalHistory());
                patientDTO.setAllergies(recentPatient.getAllergies());
                patientDTO.setCurrentMedications(recentPatient.getCurrentMedications());
                patientDTO.setCreatedAt(Date.from(recentPatient.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
                return patientDTO;
            }).collect(Collectors.toList());
            model.addAttribute("patients", collect);
            return "history";
        } catch (Exception e) {
            logger.error("Error fetching patient history: ", e);
            model.addAttribute("error", "Unable to load patient history");
            return "history";
        }
    }

    // API endpoint to get patient history as JSON
    @GetMapping("/api/history")
    @ResponseBody
    public ResponseEntity<List<Patient>> getPatientHistoryApi() {
        try {
            List<Patient> recentPatients = patientRepository.findTop10ByOrderByCreatedAtDesc();
            return ResponseEntity.ok(recentPatients);
        } catch (Exception e) {
            logger.error("Error fetching patient history via API: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search patients by name
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Patient> patients = patientRepository.findByNameContainingIgnoreCase(name.trim());
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            logger.error("Error searching patients: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get patient statistics (additional feature)
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPatientStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long totalPatients = patientRepository.count();
            stats.put("totalPatients", totalPatients);
            
            // Age group statistics
            long children = patientRepository.countByAgeRange(0, 12);
            long teens = patientRepository.countByAgeRange(13, 19);
            long adults = patientRepository.countByAgeRange(20, 59);
            long seniors = patientRepository.countByAgeRange(60, 150);
            
            Map<String, Long> ageGroups = new HashMap<>();
            ageGroups.put("children", children);
            ageGroups.put("teens", teens);
            ageGroups.put("adults", adults);
            ageGroups.put("seniors", seniors);
            
            stats.put("ageGroups", ageGroups);
            
            // Recent patients (last 30 days)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<Patient> recentPatients = patientRepository.findRecentPatients(thirtyDaysAgo);
            stats.put("recentPatientsCount", recentPatients.size());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching patient statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("service", "Medical Diagnosis Application");
        return ResponseEntity.ok(health);
    }

    // Error handling
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unhandled exception: ", e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
