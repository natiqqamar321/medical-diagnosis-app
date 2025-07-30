package com.example.diagnosis.repository;

import com.example.diagnosis.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    
    // Find patients by name (case-insensitive)
    List<Patient> findByNameContainingIgnoreCase(String name);
    
    // Find patients by age range
    List<Patient> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // Find patients created within a date range
    List<Patient> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find patients by symptoms containing specific keywords
    @Query("{ 'symptoms' : { $regex: ?0, $options: 'i' } }")
    List<Patient> findBySymptomsContaining(String symptom);
    
    // Find recent patients (last 30 days)
    @Query("{ 'createdAt' : { $gte: ?0 } }")
    List<Patient> findRecentPatients(LocalDateTime thirtyDaysAgo);
    
    // Count patients by age group
    @Query(value = "{ 'age' : { $gte: ?0, $lte: ?1 } }", count = true)
    long countByAgeRange(Integer minAge, Integer maxAge);
    
    // Find top 10 most recent patients
    List<Patient> findTop10ByOrderByCreatedAtDesc();
}
