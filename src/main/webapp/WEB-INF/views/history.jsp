<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient History - Medical Diagnosis System</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <h1>Patient History</h1>
            <p class="subtitle">Recent Patient Records & Diagnosis History</p>
        </header>

        <div class="navigation">
            <a href="${pageContext.request.contextPath}/" class="nav-link">← Back to Diagnosis</a>
            <div class="search-container">
                <input type="text" id="search-input" placeholder="Search patients by name...">
                <button id="search-btn" class="btn-search">Search</button>
            </div>
        </div>

        <div class="main-content">
            <c:if test="${not empty error}">
                <div class="error-message">
                    <p>${error}</p>
                </div>
            </c:if>

            <div class="stats-section">
                <div class="stats-grid">
                    <div class="stat-card">
                        <h3 id="total-patients">-</h3>
                        <p>Total Patients</p>
                    </div>
                    <div class="stat-card">
                        <h3 id="recent-patients">-</h3>
                        <p>Recent (30 days)</p>
                    </div>
                    <div class="stat-card">
                        <h3 id="avg-age">-</h3>
                        <p>Average Age</p>
                    </div>
                    <div class="stat-card">
                        <h3 id="emergency-cases">-</h3>
                        <p>Emergency Cases</p>
                    </div>
                </div>
            </div>

            <div class="patients-section">
                <div class="section-header">
                    <h2>Recent Patients</h2>
                    <button id="refresh-btn" class="btn-secondary">Refresh</button>
                </div>

                <div id="patients-container">
                    <c:choose>
                        <c:when test="${not empty patients}">
                            <div class="patients-grid">
                                <c:forEach var="patient" items="${patients}">
                                    <div class="patient-card">
                                        <div class="patient-header">
                                            <h3>${patient.name}</h3>
                                            <span class="patient-date">
                                                <fmt:formatDate value="${patient.createdAt}" pattern="MMM dd yyyy HH:mm" type="both"/>
                                            </span>
                                        </div>
                                        <div class="patient-details">
                                            <div class="detail-row">
                                                <span class="label">Age:</span>
                                                <span class="value">${patient.age} years</span>
                                            </div>
                                            <div class="detail-row">
                                                <span class="label">Weight:</span>
                                                <span class="value">${patient.weight} kg</span>
                                            </div>
                                            <c:if test="${not empty patient.gender}">
                                                <div class="detail-row">
                                                    <span class="label">Gender:</span>
                                                    <span class="value">${patient.gender}</span>
                                                </div>
                                            </c:if>
                                            <div class="detail-row">
                                                <span class="label">Blood Pressure:</span>
                                                <span class="value">${patient.bloodPressure} mmHg</span>
                                            </div>
                                            <div class="detail-row">
                                                <span class="label">Pulse:</span>
                                                <span class="value">${patient.pulse} bpm</span>
                                            </div>
                                            <div class="detail-row">
                                                <span class="label">Temperature:</span>
                                                <span class="value">${patient.temperature} °F</span>
                                            </div>
                                            <div class="detail-row">
                                                <span class="label">Symptoms:</span>
                                                <span class="value symptoms-text">${patient.symptoms}</span>
                                            </div>
                                        </div>
                                        <div class="patient-actions">
                                            <button class="btn-view" onclick="viewPatientDetails('${patient.id}')">
                                                View Details
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <h3>No Patient Records Found</h3>
                                <p>No patients have been diagnosed yet.</p>
                                <a href="${pageContext.request.contextPath}/" class="btn-primary">Start New Diagnosis</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div id="search-results" class="search-results" style="display: none;">
                <div class="section-header">
                    <h2>Search Results</h2>
                    <button id="clear-search" class="btn-secondary">Clear Search</button>
                </div>
                <div id="search-results-container">
                    <!-- Search results will be populated here -->
                </div>
            </div>
        </div>
    </div>

    <!-- Patient Details Modal -->
    <div id="patient-modal" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Patient Details</h2>
                <button class="close-modal">&times;</button>
            </div>
            <div id="modal-body" class="modal-body">
                <!-- Patient details will be loaded here -->
            </div>
        </div>
    </div>

    <footer class="footer">
        <p>&copy; 2025 Medical Diagnosis System. Patient data is stored securely and confidentially.</p>
    </footer>

    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/history.js"></script>
</body>
</html>
