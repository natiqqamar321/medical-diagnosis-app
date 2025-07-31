# Medical Diagnosis System

A comprehensive, AI-powered medical diagnosis application built with Spring Boot, JSP, MongoDB, and jQuery AJAX. This production-ready system allows healthcare professionals to input patient information and receive detailed AI-generated diagnosis reports.

## 🚀 Features

### Core Functionality
- **AI-Powered Diagnosis**: Advanced AI analysis using OpenRouter API with Claude Sonnet model
- **Patient Management**: Complete patient information storage and retrieval
- **Comprehensive Reports**: Detailed diagnosis including disease, causes, treatments, and medications
- **Emergency Detection**: Automatic identification of conditions requiring immediate attention
- **Patient History**: Secure storage and tracking of all patient records
- **Search & Filter**: Advanced search capabilities for patient records
- **Responsive Design**: Modern, mobile-friendly interface

### Additional Features
- **Real-time Validation**: Client and server-side form validation
- **Auto-save**: Automatic form data preservation
- **Print Reports**: Professional diagnosis report printing
- **Statistics Dashboard**: Patient analytics and insights
- **Error Handling**: Comprehensive error management and user feedback
- **Security**: Input sanitization and data protection

## 🛠️ Technology Stack

- **Backend**: Spring Boot 2.7.14, Java 11
- **Frontend**: JSP, HTML5, CSS3, jQuery 3.6.0
- **Database**: MongoDB
- **AI Integration**: OpenRouter API (Claude Sonnet)
- **Build Tool**: Maven
- **Server**: Embedded Tomcat

## 📋 Prerequisites

Before running the application, ensure you have:

1. **Java 11** or higher installed
2. **Maven 3.6+** installed
3. **MongoDB** running on localhost:27017
4. **OpenRouter API Key** (provided in the application)

## 🚀 Quick Start

### 1. Clone and Setup
```bash
# Extract the provided zip file or clone the repository
cd MedicalDiagnosisApp

# Verify Java and Maven installation
java -version
mvn -version
```

### 2. Database Setup
```bash
# Start MongoDB service
# On Windows:
net start MongoDB

# On macOS/Linux:
sudo systemctl start mongod
# or
brew services start mongodb-community

# Verify MongoDB is running
mongo --eval "db.adminCommand('ismaster')"
```

### 3. Build and Run
```bash
# Clean and compile the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Alternative: Build JAR and run
mvn clean package
java -jar target/medical-diagnosis-app-1.0.0.jar
```

### 4. Access the Application
- **Main Application**: http://localhost:8080
- **Patient History**: http://localhost:8080/history
- **Health Check**: http://localhost:8080/health
- **API Statistics**: http://localhost:8080/api/stats

## 📁 Project Structure

```
MedicalDiagnosisApp/
├── src/
│   ├── main/
│   │   ├── java/com/example/diagnosis/
│   │   │   ├── DiagnosisApplication.java          # Main application class
│   │   │   ├── controller/
│   │   │   │   └── PatientController.java         # REST controllers
│   │   │   ├── model/
│   │   │   │   ├── Patient.java                   # Patient entity
│   │   │   │   └── DiagnosisResult.java          # Diagnosis result model
│   │   │   ├── repository/
│   │   │   │   └── PatientRepository.java         # MongoDB repository
│   │   │   └── service/
│   │   │       └── DiagnosisService.java          # AI diagnosis service
│   │   ├── resources/
│   │   │   └── application.properties             # Configuration
│   │   └── webapp/
│   │       ├── WEB-INF/views/
│   │       │   ├── index.jsp                      # Main diagnosis page
│   │       │   └── history.jsp                    # Patient history page
│   │       └── static/
│   │           ├── css/style.css                  # Responsive styling
│   │           └── js/
│   │               ├── main.js                    # Main page functionality
│   │               └── history.js                 # History page functionality
├── pom.xml                                        # Maven dependencies
└── README.md                                      # This file
```

## 🔧 Configuration

### Application Properties
The application can be configured via `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/medicaldiagnosis

# JSP Configuration
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp

# Logging Configuration
logging.level.com.example.diagnosis=INFO
```

### Environment Variables
You can override configuration using environment variables:
```bash
export SERVER_PORT=8080
export MONGODB_URI=mongodb://localhost:27017/medicaldiagnosis
```

## 🔐 API Integration

The application uses OpenRouter API for AI-powered diagnosis. The API key is already configured in the `DiagnosisService.java` file.

### API Endpoints

#### Patient Management
- `GET /` - Main diagnosis form
- `POST /diagnose` - Submit patient data for diagnosis
- `GET /history` - Patient history page
- `GET /api/history` - Get patient history as JSON
- `GET /api/search?name={name}` - Search patients by name
- `GET /api/stats` - Get patient statistics
- `GET /health` - Application health check

## 🎨 User Interface

### Main Features
1. **Diagnosis Form**: Comprehensive patient information input
2. **Real-time Validation**: Immediate feedback on form inputs
3. **AI Analysis**: Professional diagnosis reports
4. **Patient History**: Searchable patient records
5. **Statistics Dashboard**: Analytics and insights
6. **Responsive Design**: Works on all devices

### Design Principles
- **Clean & Modern**: Minimalist design with focus on usability
- **Accessible**: WCAG compliant with proper contrast and navigation
- **Responsive**: Mobile-first design approach
- **Professional**: Medical-grade interface suitable for healthcare

## 🚀 Deployment

### Local Development
```bash
mvn spring-boot:run
```

### Production Deployment

#### Option 1: JAR Deployment
```bash
# Build production JAR
mvn clean package -Dmaven.test.skip=true

# Run in production
java -jar target/medical-diagnosis-app-1.0.0.jar --spring.profiles.active=prod
```

#### Option 2: Docker Deployment
```dockerfile
FROM openjdk:11-jre-slim
COPY target/medical-diagnosis-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### Option 3: Traditional Server Deployment
- Build WAR file by changing packaging in pom.xml
- Deploy to Tomcat, JBoss, or similar application server

### Production Considerations
1. **Database**: Use MongoDB Atlas or dedicated MongoDB server
2. **Security**: Implement HTTPS and authentication
3. **Monitoring**: Add application monitoring and logging
4. **Backup**: Regular database backups
5. **Scaling**: Consider load balancing for high traffic

## 🧪 Testing

### Manual Testing
1. Start the application
2. Navigate to http://localhost:8080
3. Fill out the patient form with test data
4. Submit for diagnosis
5. Verify AI-generated results
6. Check patient history functionality

### Test Data Examples
```
Patient Name: John Doe
Age: 35
Weight: 75.5
Gender: Male
Vitals: BP: 120/80, Pulse: 72, Temp: 98.6°F, Resp: 16
Symptoms: Persistent headache, fatigue, mild fever for 3 days
Medical History: No significant medical history
Allergies: None known
Current Medications: None
```

## 🔍 Troubleshooting

### Common Issues

#### MongoDB Connection Error
```bash
# Check if MongoDB is running
mongo --eval "db.adminCommand('ismaster')"

# Start MongoDB service
sudo systemctl start mongod
```

#### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

#### AI API Issues
- Verify OpenRouter API key is valid
- Check internet connectivity
- Monitor API rate limits

#### JSP Not Loading
- Ensure JSP dependencies are in pom.xml
- Check view resolver configuration
- Verify JSP files are in correct directory

## 📊 Performance

### Optimization Tips
1. **Database Indexing**: Add indexes for frequently queried fields
2. **Caching**: Implement Redis for session management
3. **CDN**: Use CDN for static assets
4. **Compression**: Enable GZIP compression
5. **Connection Pooling**: Configure database connection pooling

### Monitoring
- Application logs: `logs/application.log`
- MongoDB logs: Check MongoDB log files
- JVM metrics: Use JVisualVM or similar tools
- API response times: Monitor OpenRouter API calls

## 🔒 Security

### Implemented Security Measures
1. **Input Validation**: Server-side validation for all inputs
2. **XSS Protection**: Output encoding in JSP pages
3. **CSRF Protection**: Spring Security CSRF tokens
4. **SQL Injection**: MongoDB queries are parameterized
5. **Error Handling**: Secure error messages

### Additional Security Recommendations
1. Implement user authentication and authorization
2. Use HTTPS in production
3. Regular security updates
4. API rate limiting
5. Data encryption at rest

## 📝 License

This project is for educational and demonstration purposes. Please ensure compliance with medical software regulations in your jurisdiction before using in production healthcare environments.

## 🤝 Support

For technical support or questions:
1. Check the troubleshooting section
2. Review application logs
3. Verify configuration settings
4. Test with sample data

## 🔄 Updates and Maintenance

### Regular Maintenance Tasks
1. Update dependencies regularly
2. Monitor API usage and costs
3. Backup patient data
4. Review and update AI prompts
5. Performance monitoring and optimization

---

**⚠️ Medical Disclaimer**: This application is for educational and demonstration purposes only. AI-generated diagnoses should not replace professional medical advice. Always consult qualified healthcare professionals for medical decisions.
