package com.example.diagnosis.model;

import java.util.List;
import java.util.ArrayList;

public class DiagnosisResult {
    
    private String disease;
    private String cause;
    private String cure;
    private List<String> medicines;
    private String severity;
    private String recommendations;
    private String precautions;
    private boolean requiresImmediateAttention;
    
    // Default constructor
    public DiagnosisResult() {
        this.medicines = new ArrayList<>();
        this.requiresImmediateAttention = false;
    }
    
    // Constructor with basic fields
    public DiagnosisResult(String disease, String cause, String cure, List<String> medicines) {
        this();
        this.disease = disease;
        this.cause = cause;
        this.cure = cure;
        this.medicines = medicines != null ? medicines : new ArrayList<>();
    }
    
    // Getters and Setters
    public String getDisease() {
        return disease;
    }
    
    public void setDisease(String disease) {
        this.disease = disease;
    }
    
    public String getCause() {
        return cause;
    }
    
    public void setCause(String cause) {
        this.cause = cause;
    }
    
    public String getCure() {
        return cure;
    }
    
    public void setCure(String cure) {
        this.cure = cure;
    }
    
    public List<String> getMedicines() {
        return medicines;
    }
    
    public void setMedicines(List<String> medicines) {
        this.medicines = medicines != null ? medicines : new ArrayList<>();
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    
    public String getPrecautions() {
        return precautions;
    }
    
    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }
    
    public boolean isRequiresImmediateAttention() {
        return requiresImmediateAttention;
    }
    
    public void setRequiresImmediateAttention(boolean requiresImmediateAttention) {
        this.requiresImmediateAttention = requiresImmediateAttention;
    }
    
    // Helper method to add medicine
    public void addMedicine(String medicine) {
        if (this.medicines == null) {
            this.medicines = new ArrayList<>();
        }
        this.medicines.add(medicine);
    }
    
    @Override
    public String toString() {
        return "DiagnosisResult{" +
                "disease='" + disease + '\'' +
                ", cause='" + cause + '\'' +
                ", cure='" + cure + '\'' +
                ", medicines=" + medicines +
                ", severity='" + severity + '\'' +
                ", recommendations='" + recommendations + '\'' +
                ", precautions='" + precautions + '\'' +
                ", requiresImmediateAttention=" + requiresImmediateAttention +
                '}';
    }
}
