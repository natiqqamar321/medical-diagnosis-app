$(document).ready(function() {
    // Initialize the history page
    initializeHistoryPage();
    
    // Load patient statistics
    loadPatientStats();
    
    // Search functionality
    $('#search-btn').click(function() {
        performSearch();
    });
    
    $('#search-input').keypress(function(e) {
        if (e.which === 13) { // Enter key
            performSearch();
        }
    });
    
    // Clear search
    $('#clear-search').click(function() {
        clearSearch();
    });
    
    // Refresh data
    $('#refresh-btn').click(function() {
        refreshPatientData();
    });
    
    // Modal functionality
    $('.close-modal').click(function() {
        closeModal();
    });
    
    // Close modal when clicking outside
    $('#patient-modal').click(function(e) {
        if (e.target === this) {
            closeModal();
        }
    });
});

function initializeHistoryPage() {
    console.log('Patient History page initialized');
    
    // Add hover effects to patient cards
    $(document).on('mouseenter', '.patient-card', function() {
        $(this).addClass('hover-effect');
    }).on('mouseleave', '.patient-card', function() {
        $(this).removeClass('hover-effect');
    });
    
    // Add click to expand symptoms
    $(document).on('click', '.symptoms-text', function() {
        const fullText = $(this).attr('title') || $(this).text();
        if (fullText.length > 50) {
            $(this).toggleClass('expanded');
            if ($(this).hasClass('expanded')) {
                $(this).css({
                    'white-space': 'normal',
                    'text-overflow': 'initial',
                    'overflow': 'visible'
                });
            } else {
                $(this).css({
                    'white-space': 'nowrap',
                    'text-overflow': 'ellipsis',
                    'overflow': 'hidden'
                });
            }
        }
    });
}

function loadPatientStats() {
    $.ajax({
        url: 'api/stats',
        type: 'GET',
        success: function(stats) {
            updateStatsDisplay(stats);
        },
        error: function(xhr, status, error) {
            console.error('Error loading patient stats:', error);
            showStatsError();
        }
    });
}

function updateStatsDisplay(stats) {
    $('#total-patients').text(stats.totalPatients || 0);
    $('#recent-patients').text(stats.recentPatientsCount || 0);
    
    // Calculate average age (this would need to be added to the backend)
    $('#avg-age').text('N/A');
    
    // Count emergency cases (this would need to be tracked)
    $('#emergency-cases').text('N/A');
    
    // Update age group breakdown if available
    if (stats.ageGroups) {
        console.log('Age groups:', stats.ageGroups);
        // Could add a chart here in the future
    }
}

function showStatsError() {
    $('.stat-card h3').text('--');
    console.log('Unable to load patient statistics');
}

function performSearch() {
    const searchTerm = $('#search-input').val().trim();
    
    if (!searchTerm) {
        showSearchError('Please enter a search term');
        return;
    }
    
    if (searchTerm.length < 2) {
        showSearchError('Search term must be at least 2 characters');
        return;
    }
    
    // Show loading
    $('#search-btn').prop('disabled', true).text('Searching...');
    
    $.ajax({
        url: 'api/search',
        type: 'GET',
        data: { name: searchTerm },
        success: function(patients) {
            displaySearchResults(patients, searchTerm);
        },
        error: function(xhr, status, error) {
            console.error('Search error:', error);
            showSearchError('Search failed. Please try again.');
        },
        complete: function() {
            $('#search-btn').prop('disabled', false).text('Search');
        }
    });
}

function displaySearchResults(patients, searchTerm) {
    if (patients.length === 0) {
        const noResultsHtml = `
            <div class="empty-state">
                <h3>No Results Found</h3>
                <p>No patients found matching "${searchTerm}"</p>
            </div>
        `;
        $('#search-results-container').html(noResultsHtml);
    } else {
        const resultsHtml = buildPatientsGrid(patients);
        $('#search-results-container').html(resultsHtml);
    }
    
    $('#search-results').show();
    
    // Scroll to results
    $('html, body').animate({
        scrollTop: $('#search-results').offset().top - 20
    }, 500);
}

function buildPatientsGrid(patients) {
    let html = '<div class="patients-grid">';
    
    patients.forEach(function(patient) {
        html += buildPatientCard(patient);
    });
    
    html += '</div>';
    return html;
}

function buildPatientCard(patient) {
    const createdDate = new Date(patient.createdAt).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
    
    return `
        <div class="patient-card">
            <div class="patient-header">
                <h3>${patient.name}</h3>
                <span class="patient-date">${createdDate}</span>
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
                ${patient.gender ? `
                <div class="detail-row">
                    <span class="label">Gender:</span>
                    <span class="value">${patient.gender}</span>
                </div>
                ` : ''}
                <div class="detail-row">
                    <span class="label">Vitals:</span>
                    <span class="value">${patient.vitals}</span>
                </div>
                <div class="detail-row">
                    <span class="label">Symptoms:</span>
                    <span class="value symptoms-text" title="${patient.symptoms}">${truncateText(patient.symptoms, 50)}</span>
                </div>
            </div>
            <div class="patient-actions">
                <button class="btn-view" onclick="viewPatientDetails('${patient.id}')">
                    View Details
                </button>
            </div>
        </div>
    `;
}

function truncateText(text, maxLength) {
    if (text.length <= maxLength) {
        return text;
    }
    return text.substring(0, maxLength) + '...';
}

function showSearchError(message) {
    const errorHtml = `
        <div class="error-message">
            ${message}
        </div>
    `;
    $('#search-results-container').html(errorHtml);
    $('#search-results').show();
}

function clearSearch() {
    $('#search-input').val('');
    $('#search-results').hide();
    $('#search-results-container').empty();
}

function refreshPatientData() {
    // Show loading state
    $('#refresh-btn').prop('disabled', true).text('Refreshing...');
    
    // Reload the page to get fresh data
    setTimeout(function() {
        location.reload();
    }, 500);
}

function viewPatientDetails(patientId) {
    // For now, we'll show a modal with available patient information
    // In a full implementation, you might fetch additional details from the server
    
    const patientCard = $(`.patient-card:has(button[onclick*="${patientId}"])`);
    if (patientCard.length === 0) {
        showError('Patient details not found');
        return;
    }
    
    // Extract patient information from the card
    const patientName = patientCard.find('.patient-header h3').text();
    const patientDate = patientCard.find('.patient-date').text();
    const patientDetails = extractPatientDetails(patientCard);
    
    const modalContent = buildPatientDetailsModal(patientName, patientDate, patientDetails);
    $('#modal-body').html(modalContent);
    $('#patient-modal').fadeIn();
}

function extractPatientDetails(patientCard) {
    const details = {};
    
    patientCard.find('.detail-row').each(function() {
        const label = $(this).find('.label').text().replace(':', '');
        const value = $(this).find('.value').text();
        details[label] = value;
    });
    
    return details;
}

function buildPatientDetailsModal(name, date, details) {
    let html = `
        <div class="patient-details-modal">
            <div class="patient-info-header">
                <h3>${name}</h3>
                <p class="date-info">Recorded on: ${date}</p>
            </div>
            
            <div class="details-grid">
    `;
    
    Object.keys(details).forEach(function(label) {
        html += `
            <div class="detail-item">
                <strong>${label}:</strong>
                <span>${details[label]}</span>
            </div>
        `;
    });
    
    html += `
            </div>
            
            <div class="modal-actions">
                <button class="btn-secondary" onclick="printPatientDetails('${name}', '${date}')">
                    Print Details
                </button>
                <button class="btn-primary" onclick="closeModal()">
                    Close
                </button>
            </div>
        </div>
    `;
    
    return html;
}

function printPatientDetails(name, date) {
    const modalContent = $('#modal-body').html();
    const printWindow = window.open('', '_blank');
    
    printWindow.document.write(`
        <html>
        <head>
            <title>Patient Details - ${name}</title>
            <style>
                body { 
                    font-family: Arial, sans-serif; 
                    margin: 20px; 
                    line-height: 1.6;
                }
                .patient-info-header { 
                    text-align: center; 
                    margin-bottom: 30px; 
                    border-bottom: 2px solid #333; 
                    padding-bottom: 15px; 
                }
                .patient-info-header h3 { 
                    font-size: 24px; 
                    margin-bottom: 5px; 
                }
                .date-info { 
                    color: #666; 
                    font-style: italic; 
                }
                .details-grid { 
                    display: grid; 
                    gap: 15px; 
                }
                .detail-item { 
                    padding: 10px; 
                    border: 1px solid #ddd; 
                    border-radius: 4px; 
                }
                .detail-item strong { 
                    display: block; 
                    margin-bottom: 5px; 
                    color: #333; 
                }
                .modal-actions { 
                    display: none; 
                }
            </style>
        </head>
        <body>
            ${modalContent}
        </body>
        </html>
    `);
    
    printWindow.document.close();
    printWindow.print();
}

function closeModal() {
    $('#patient-modal').fadeOut();
}

function showError(message) {
    const errorHtml = `
        <div class="error-message">
            ${message}
        </div>
    `;
    
    // Show error in a temporary overlay
    $('body').append(`
        <div class="error-overlay" style="
            position: fixed; 
            top: 20px; 
            right: 20px; 
            z-index: 2000; 
            max-width: 300px;
        ">
            ${errorHtml}
        </div>
    `);
    
    setTimeout(function() {
        $('.error-overlay').fadeOut(function() {
            $(this).remove();
        });
    }, 5000);
}

// Add custom styles for the history page
$('<style>')
    .prop('type', 'text/css')
    .html(`
        .hover-effect {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15) !important;
        }
        
        .symptoms-text {
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .symptoms-text:hover {
            color: #667eea;
        }
        
        .patient-details-modal {
            max-width: 100%;
        }
        
        .patient-info-header {
            text-align: center;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 1px solid #e2e8f0;
        }
        
        .patient-info-header h3 {
            font-size: 1.5rem;
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 5px;
        }
        
        .date-info {
            color: #718096;
            font-style: italic;
        }
        
        .details-grid {
            display: grid;
            gap: 15px;
            margin-bottom: 25px;
        }
        
        .detail-item {
            padding: 15px;
            background-color: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
        }
        
        .detail-item strong {
            display: block;
            margin-bottom: 8px;
            color: #4a5568;
            font-weight: 600;
        }
        
        .detail-item span {
            color: #2d3748;
            line-height: 1.5;
        }
        
        .modal-actions {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 20px;
        }
        
        .error-overlay .error-message {
            margin: 0;
        }
        
        .search-results {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #e2e8f0;
        }
    `)
    .appendTo('head');
