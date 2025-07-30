package com.example.diagnosis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDateTime;

@Document(collection = "patients")
public class Patient {
    
    @Id
    private String id;
    
    @NotBlank(message = "Patient name is required")
    private String name;
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    private Integer age;
    
    @NotNull(message = "Weight is required")
    @Min(value = 0, message = "Weight must be positive")
    private Integer weight;
    
    @NotBlank(message = "Blood Pressure is required")
    private String bloodPressure;

    @NotNull(message = "Pulse is required")
    @Min(value = 30, message = "Pulse must be at least 30 bpm")
    @Max(value = 200, message = "Pulse must be at most 200 bpm")
    private Integer pulse;

    @NotNull(message = "Temperature is required")
    @Min(value = 90, message = "Temperature must be at least 90°F")
    @Max(value = 110, message = "Temperature must be at most 110°F")
    private Double temperature;

    @NotNull(message = "Respiration rate is required")
    @Min(value = 5, message = "Respiration rate must be at least 5 breaths per minute")
    @Max(value = 60, message = "Respiration rate must be at most 60 breaths per minute")
    private Integer respirationRate;
    
    @NotBlank(message = "Symptoms are required")
    private String symptoms;
    
    private LocalDateTime createdAt;
    
    private String gender;
    private String medicalHistory;
    private String allergies;
    private String currentMedications;
    
    // Default constructor
    public Patient() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with required fields
    public Patient(String name, Integer age, Integer weight, String symptoms, String bloodPressure, Integer pulse, Double temperature, Integer respirationRate) {
        this();
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.symptoms = symptoms;
        this.bloodPressure = bloodPressure;
        this.pulse = pulse;
        this.temperature = temperature;
        this.respirationRate = respirationRate;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public String getBloodPressure() {
        return bloodPressure;
    }
    
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
    
    public Integer getPulse() {
        return pulse;
    }
    
    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Integer getRespirationRate() {
        return respirationRate;
    }
    
    public void setRespirationRate(Integer respirationRate) {
        this.respirationRate = respirationRate;
    }
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    public String getCurrentMedications() {
        return currentMedications;
    }
    
    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
    }
    
    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", pulse=" + pulse +
                ", temperature=" + temperature +
                ", respirationRate=" + respirationRate +
                ", symptoms='" + symptoms + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
