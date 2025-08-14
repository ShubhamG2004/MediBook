CREATE DATABASE hospital_db;
USE hospital_db;

-- 1. User table (for login)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin','doctor','patient') NOT NULL
);

-- 2. Doctors table
CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL
);

-- 3. Patients table
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male','Female','Other') NOT NULL
);

-- 4. Appointments table
CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT,
    patient_id INT,
    appointment_date DATE,
    appointment_time TIME,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);

INSERT INTO users (username, password, role) VALUES
('admin1', SHA2('admin123', 256), 'admin'),
('doctor1', SHA2('docpass', 256), 'doctor'),
('patient1', SHA2('patientpass', 256), 'patient');

SELECT * FROM users;

INSERT INTO users (username, password, role) VALUES
('admin2', 'admin123', 'admin'),
('doctor2', 'docpass', 'doctor'),
('patient2', 'patientpass', 'patient');
