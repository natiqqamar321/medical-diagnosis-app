$(document).ready(function() {
    // Initialize the application
    initializeApp();
    
    // Form submission handler
    $('#diagnosis-form').submit(function(e) {
        e.preventDefault();
        performDiagnosis();
    });
    
    // Clear form handler
    $('#clear-btn').click(function() {
        clearForm();
    });
    
    // Close result handler
    $('#close-result').click(function() {
        $('#result-section').slideUp();
    });
    
    // Form validation on input
    $('input, textarea, select').on('blur', function() {
        validateField($(this));
    });
});

function initializeApp() {
    console.log('Medical Diagnosis System initialized');
    
    // Add input formatting for integer weight
    $('#weight').on('input', function() {
        let value = $(this).val();
        if (value && !isNaN(value)) {
            $(this).val(Math.floor(parseFloat(value)));
        }
    });
    
    // Add character counter for symptoms
    $('#symptoms').on('input', function() {
        let length = $(this).val().length;
        let counter = $('#symptoms-counter');
        if (counter.length === 0) {
            $(this).after('<div id="symptoms-counter" class="char-counter"></div>');
            counter = $('#symptoms-counter');
        }
        counter.text(length + ' characters');
        
        if (length < 10) {
            counter.css('color', '#e53e3e');
        } else if (length < 50) {
            counter.css('color', '#d69e2e');
        } else {
            counter.css('color', '#38a169');
        }
    });
    
    // Auto-save form data to localStorage
    $('input, textarea, select').on('input change', function() {
        saveFormData();
    });
    
    // Load saved form data
    loadFormData();
}

function performDiagnosis() {
    // Show loading state
    showLoading();
    
    // Validate form
    if (!validateForm()) {
        hideLoading();
        return;
    }
    
    // Gather form data
    const formData = gatherFormData();
    
    // Log the request
    console.log('Submitting diagnosis request:', formData);
    
    // Make AJAX request
    $.ajax({
        url: 'diagnose',
        type: 'POST',
        data: formData,
        timeout: 60000, // 60 seconds timeout
        success: function(response) {
            console.log('Diagnosis response:', response);
            hideLoading();
            displayResults(response);
            clearFormData(); // Clear saved data after successful submission
        },
        error: function(xhr, status, error) {
            console.error('Diagnosis error:', xhr.responseText);
            hideLoading();
            handleError(xhr, status, error);
        }
    });
}

function gatherFormData() {
    return {
        name: $('#name').val().trim(),
        age: parseInt($('#age').val()),
        weight: parseInt($('#weight').val()),
        gender: $('#gender').val(),
        bloodPressure: $('#bloodPressure').val().trim(),
        pulse: parseInt($('#pulse').val()),
        temperature: parseFloat($('#temperature').val()),
        respirationRate: parseInt($('#respirationRate').val()),
        symptoms: $('#symptoms').val().trim(),
        medicalHistory: $('#medicalHistory').val().trim(),
        allergies: $('#allergies').val().trim(),
        currentMedications: $('#currentMedications').val().trim()
    };
}

function validateForm() {
    let isValid = true;
    const requiredFields = ['name', 'age', 'weight', 'bloodPressure', 'pulse', 'temperature', 'respirationRate', 'symptoms'];
    
    // Clear previous errors
    $('.field-error').remove();
    $('.form-group').removeClass('error');
    
    requiredFields.forEach(function(fieldName) {
        const field = $('#' + fieldName);
        if (!validateField(field)) {
            isValid = false;
        }
    });
    
    // Additional validations
    const age = parseInt($('#age').val());
    if (age && (age < 0 || age > 150)) {
        showFieldError('#age', 'Age must be between 0 and 150');
        isValid = false;
    }
    
    const weight = parseInt($('#weight').val());
    if (weight && weight <= 0) {
        showFieldError('#weight', 'Weight must be greater than 0');
        isValid = false;
    }
    
    const bloodPressure = $('#bloodPressure').val().trim();
    const bpPattern = /^\d{2,3}\/\d{2,3}$/;
    if (bloodPressure && !bpPattern.test(bloodPressure)) {
        showFieldError('#bloodPressure', 'Blood Pressure must be in format systolic/diastolic e.g., 120/80');
        isValid = false;
    }
    
    const symptoms = $('#symptoms').val().trim();
    if (symptoms && symptoms.length < 10) {
        showFieldError('#symptoms', 'Please provide more detailed symptoms (at least 10 characters)');
        isValid = false;
    }
    
    return isValid;
}

function validateField(field) {
    const value = field.val().trim();
    const fieldName = field.attr('name');
    const isRequired = field.prop('required');
    
    // Clear previous error
    field.parent().find('.field-error').remove();
    field.parent().removeClass('error');
    
    if (isRequired && !value) {
        showFieldError('#' + field.attr('id'), 'This field is required');
        return false;
    }
    
    return true;
}

function showFieldError(selector, message) {
    const field = $(selector);
    field.parent().addClass('error');
    field.after('<div class="field-error">' + message + '</div>');
}

function displayResults(result) {
    const resultHtml = buildResultHtml(result);
    $('#result-content').html(resultHtml);
    $('#result-section').slideDown();
    
    // Scroll to results
    $('html, body').animate({
        scrollTop: $('#result-section').offset().top - 20
    }, 500);
    
    // Add print functionality
    addPrintButton();
}

function buildResultHtml(result) {
    let html = '';
    
    // Emergency alert
    if (result.requiresImmediateAttention) {
        html += '<div class="emergency-alert">';
        html += '⚠️ URGENT: This condition may require immediate medical attention. Please consult a healthcare professional immediately.';
        html += '</div>';
    }
    
    // Disease diagnosis
    html += '<div class="diagnosis-card">';
    html += '<h3>🔍 Diagnosis</h3>';
    html += '<p><strong>' + (result.disease || 'Unable to determine') + '</strong></p>';
    if (result.severity) {
        html += '<span class="severity-badge severity-' + result.severity.toLowerCase() + '">' + result.severity + '</span>';
    }
    html += '</div>';
    
    // Cause
    if (result.cause) {
        html += '<div class="diagnosis-card">';
        html += '<h3>🧬 Possible Cause</h3>';
        html += '<p>' + result.cause + '</p>';
        html += '</div>';
    }
    
    // Treatment/Cure
    if (result.cure) {
        html += '<div class="diagnosis-card">';
        html += '<h3>💊 Treatment Approach</h3>';
        html += '<p>' + result.cure + '</p>';
        html += '</div>';
    }
    
    // Medicines
    if (result.medicines && result.medicines.length > 0) {
        html += '<div class="diagnosis-card">';
        html += '<h3>💉 Recommended Medications</h3>';
        html += '<ul>';
        result.medicines.forEach(function(medicine) {
            html += '<li>' + medicine + '</li>';
        });
        html += '</ul>';
        html += '<p><em>Note: Please consult a healthcare professional before taking any medication.</em></p>';
        html += '</div>';
    }
    
    // Recommendations
    if (result.recommendations) {
        html += '<div class="diagnosis-card">';
        html += '<h3>📋 Recommendations</h3>';
        html += '<p>' + result.recommendations + '</p>';
        html += '</div>';
    }
    
    // Precautions
    if (result.precautions) {
        html += '<div class="diagnosis-card">';
        html += '<h3>⚠️ Precautions</h3>';
        html += '<p>' + result.precautions + '</p>';
        html += '</div>';
    }
    
    // Disclaimer
    html += '<div class="diagnosis-card" style="background-color: #fff5f5; border-color: #feb2b2;">';
    html += '<h3>⚖️ Medical Disclaimer</h3>';
    html += '<p><strong>Important:</strong> This AI-generated diagnosis is for informational purposes only and should not replace professional medical advice. Always consult with qualified healthcare professionals for proper diagnosis and treatment.</p>';
    html += '</div>';
    
    return html;
}

function addPrintButton() {
    if ($('#print-results').length === 0) {
        $('#result-content').append(
            '<div style="text-align: center; margin-top: 20px;">' +
            '<button id="print-results" class="btn-secondary">🖨️ Print Results</button>' +
            '</div>'
        );
        
        $('#print-results').click(function() {
            printResults();
        });
    }
}

function printResults() {
    const printWindow = window.open('', '_blank');
    const patientName = $('#name').val();
    const currentDate = new Date().toLocaleDateString();
    
    printWindow.document.write(`
        <html>
        <head>
            <title>Medical Diagnosis Report - ${patientName}</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                .header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 10px; }
                .diagnosis-card { margin-bottom: 20px; padding: 15px; border: 1px solid #ddd; }
                .diagnosis-card h3 { color: #333; margin-bottom: 10px; }
                .emergency-alert { background: #ffebee; color: #c62828; padding: 15px; margin-bottom: 20px; border: 1px solid #ef5350; }
                .severity-badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
                .severity-mild { background: #e8f5e8; color: #2e7d32; }
                .severity-moderate { background: #fff3e0; color: #f57c00; }
                .severity-severe { background: #ffebee; color: #c62828; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>Medical Diagnosis Report</h1>
                <p>Patient: ${patientName} | Date: ${currentDate}</p>
            </div>
            ${$('#result-content').html()}
        </body>
        </html>
    `);
    
    printWindow.document.close();
    printWindow.print();
}

function handleError(xhr, status, error) {
    let errorMessage = 'An unexpected error occurred. Please try again.';
    
    if (xhr.status === 400) {
        // Validation error
        const response = xhr.responseJSON || xhr.responseText;
        if (typeof response === 'object') {
            errorMessage = 'Please correct the following errors:<br>';
            Object.keys(response).forEach(function(field) {
                errorMessage += '• ' + response[field] + '<br>';
            });
        } else {
            errorMessage = response || 'Please check your input and try again.';
        }
    } else if (xhr.status === 500) {
        errorMessage = 'Server error occurred. Please try again later.';
    } else if (status === 'timeout') {
        errorMessage = 'Request timed out. Please check your connection and try again.';
    } else if (status === 'error') {
        errorMessage = 'Network error. Please check your internet connection.';
    }
    
    showErrorMessage(errorMessage);
}

function showErrorMessage(message) {
    const errorHtml = '<div class="error-message">' + message + '</div>';
    $('#result-content').html(errorHtml);
    $('#result-section').slideDown();
    
    // Scroll to error
    $('html, body').animate({
        scrollTop: $('#result-section').offset().top - 20
    }, 500);
}

function showLoading() {
    $('#loading-overlay').fadeIn();
    $('#submit-btn').prop('disabled', true);
    $('#submit-btn .btn-text').hide();
    $('#submit-btn .btn-loading').show();
}

function hideLoading() {
    $('#loading-overlay').fadeOut();
    $('#submit-btn').prop('disabled', false);
    $('#submit-btn .btn-text').show();
    $('#submit-btn .btn-loading').hide();
}

function clearForm() {
    $('#diagnosis-form')[0].reset();
    $('#result-section').slideUp();
    $('.field-error').remove();
    $('.form-group').removeClass('error');
    $('#symptoms-counter').remove();
    clearFormData();
    
    // Show success message
    showSuccessMessage('Form cleared successfully!');
}

function showSuccessMessage(message) {
    const successHtml = '<div class="success-message">' + message + '</div>';
    $('body').prepend(successHtml);
    
    setTimeout(function() {
        $('.success-message').fadeOut(function() {
            $(this).remove();
        });
    }, 3000);
}

function saveFormData() {
    const formData = gatherFormData();
    localStorage.setItem('diagnosisFormData', JSON.stringify(formData));
}

function loadFormData() {
    const savedData = localStorage.getItem('diagnosisFormData');
    if (savedData) {
        try {
            const formData = JSON.parse(savedData);
            Object.keys(formData).forEach(function(key) {
                if (formData[key]) {
                    $('#' + key).val(formData[key]);
                }
            });
        } catch (e) {
            console.error('Error loading saved form data:', e);
        }
    }
}

function clearFormData() {
    localStorage.removeItem('diagnosisFormData');
}

// Utility functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// Add custom styles for validation
$('<style>')
    .prop('type', 'text/css')
    .html(`
        .form-group.error input,
        .form-group.error textarea,
        .form-group.error select {
            border-color: #e53e3e !important;
            box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.1) !important;
        }
        
        .field-error {
            color: #e53e3e;
            font-size: 12px;
            margin-top: 5px;
            font-weight: 500;
        }
        
        .char-counter {
            font-size: 12px;
            text-align: right;
            margin-top: 5px;
            font-weight: 500;
        }
        
        .success-message {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            max-width: 300px;
        }
    `)
    .appendTo('head');
