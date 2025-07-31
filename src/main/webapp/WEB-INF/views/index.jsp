<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Medical Diagnosis System</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <h1>Medical Diagnosis System</h1>
            <p class="subtitle">AI-Powered Medical Analysis & Diagnosis</p>
        </header>

        <div class="main-content">
            <div class="form-container">
                <form id="diagnosis-form" class="diagnosis-form">
                    <div class="form-section">
                        <h2>Patient Information</h2>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="name">Patient Name *</label>
                                <input type="text" id="name" name="name" required 
                                       placeholder="Enter patient's full name">
                            </div>
                            
                            <div class="form-group">
                                <label for="age">Age *</label>
                                <input type="number" id="age" name="age" required 
                                       min="0" max="150" placeholder="Age in years">
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="weight">Weight (kg) *</label>
                                <input type="number" id="weight" name="weight" required 
                                       min="0" step="1" placeholder="Weight in kilograms">
                            </div>
                            
                            <div class="form-group">
                                <label for="gender">Gender</label>
                                <select id="gender" name="gender">
                                    <option value="">Select Gender</option>
                                    <option value="Male">Male</option>
                                    <option value="Female">Female</option>
                                    <option value="Other">Other</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="bloodPressure">Blood Pressure (BP) *</label>
                                <input type="text" id="bloodPressure" name="bloodPressure" required
                                       placeholder="e.g., 120/80" pattern="\d{2,3}/\d{2,3}" title="Format: systolic/diastolic e.g., 120/80">
                            </div>
                            <div class="form-group">
                                <label for="pulse">Pulse (bpm) *</label>
                                <input type="number" id="pulse" name="pulse" required min="30" max="200" placeholder="e.g., 72">
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="temperature">Temperature (°F) *</label>
                                <input type="number" id="temperature" name="temperature" required step="0.1" min="90" max="110" placeholder="e.g., 98.6">
                            </div>
                            <div class="form-group">
                                <label for="respirationRate">Respiration Rate (breaths/min) *</label>
                                <input type="number" id="respirationRate" name="respirationRate" required min="5" max="60" placeholder="e.g., 16">
                            </div>
                        </div>
                    </div>

                    <div class="form-section">
                        <h2>Medical Details</h2>
                        
                        <div class="form-group">
                            <label for="symptoms">Symptoms *</label>
                            <textarea id="symptoms" name="symptoms" required rows="4"
                                      placeholder="Describe all symptoms in detail..."></textarea>
                        </div>

                        <div class="form-group">
                            <label for="medicalHistory">Medical History</label>
                            <textarea id="medicalHistory" name="medicalHistory" rows="3"
                                      placeholder="Previous medical conditions, surgeries, etc."></textarea>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="allergies">Allergies</label>
                                <input type="text" id="allergies" name="allergies" 
                                       placeholder="Known allergies (medications, food, etc.)">
                            </div>
                            
                            <div class="form-group">
                                <label for="currentMedications">Current Medications</label>
                                <input type="text" id="currentMedications" name="currentMedications" 
                                       placeholder="Currently taking medications">
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" id="submit-btn" class="btn-primary">
                            <span class="btn-text">Analyze & Diagnose</span>
                            <span class="btn-loading" style="display: none;">Analyzing...</span>
                        </button>
                        <button type="button" id="clear-btn" class="btn-secondary">Clear Form</button>
                    </div>
                </form>
            </div>

            <div id="result-section" class="result-section" style="display: none;">
                <div class="result-header">
                    <h2>Diagnosis Results</h2>
                    <button id="close-result" class="close-btn">&times;</button>
                </div>
                <div id="result-content" class="result-content">
                    <!-- Results will be populated here -->
                </div>
            </div>
        </div>

        <div class="features-section">
            <h2>Features</h2>
            <div class="features-grid">
                <div class="feature-card">
                    <h3>AI-Powered Analysis</h3>
                    <p>Advanced AI algorithms analyze symptoms and provide accurate diagnosis</p>
                </div>
                <div class="feature-card">
                    <h3>Comprehensive Reports</h3>
                    <p>Detailed reports including disease, causes, treatments, and medications</p>
                </div>
                <div class="feature-card">
                    <h3>Patient History</h3>
                    <p>Secure storage and tracking of patient information and diagnosis history</p>
                </div>
                <div class="feature-card">
                    <h3>Emergency Detection</h3>
                    <p>Automatic detection of conditions requiring immediate medical attention</p>
                </div>
            </div>
        </div>

        <div class="quick-actions">
            <a href="${pageContext.request.contextPath}/history" class="action-link">View Patient History</a>
            <a href="${pageContext.request.contextPath}/health" class="action-link">System Health</a>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2025 Medical Diagnosis System. For educational purposes only. Always consult healthcare professionals.</p>
        <p style="font-family: 'Poppins', sans-serif; font-size: 14px; color: #444;">
            Powered by <a href="https://www.linkedin.com/in/natiq-qamar-b51a88189/" target="_blank" style="color: #0a66c2; text-decoration: none; font-weight: 600;">
            Natiq Qamar
        </a>
        </p>
    </footer>

    <!-- Loading Overlay -->
    <div id="loading-overlay" class="loading-overlay" style="display: none;">
        <div class="loading-spinner">
            <div class="spinner"></div>
            <p>Analyzing patient data...</p>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
</body>
</html>
