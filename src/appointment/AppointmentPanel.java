package appointment;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AppointmentPanel extends JFrame {
    private JComboBox<String> doctorBox, patientBox;
    private JTextField dateField, timeField;
    private JButton bookButton, refreshButton;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    public AppointmentPanel() {
        setTitle("MediBook – Appointment Booking");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Select Doctor:"));
        doctorBox = new JComboBox<>();
        formPanel.add(doctorBox);

        formPanel.add(new JLabel("Select Patient:"));
        patientBox = new JComboBox<>();
        formPanel.add(patientBox);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Time (HH:MM):"));
        timeField = new JTextField();
        formPanel.add(timeField);

        bookButton = new JButton("Book Appointment");
        refreshButton = new JButton("Refresh List");
        formPanel.add(bookButton);
        formPanel.add(refreshButton);

        // Table for appointments
        tableModel = new DefaultTableModel(new String[]{"ID", "Doctor", "Patient", "Date", "Time"}, 0);
        appointmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        bookButton.addActionListener(e -> bookAppointment());
        refreshButton.addActionListener(e -> loadAppointments());

        loadDoctors();
        loadPatients();
        loadAppointments();
    }

    private void loadDoctors() {
        doctorBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT doctor_id, name FROM doctors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                doctorBox.addItem(rs.getInt("doctor_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadPatients() {
        patientBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT patient_id, name FROM patients";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                patientBox.addItem(rs.getInt("patient_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bookAppointment() {
        String doctor = (String) doctorBox.getSelectedItem();
        String patient = (String) patientBox.getSelectedItem();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();

        if (doctor == null || patient == null || date.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        int doctorId = Integer.parseInt(doctor.split(" - ")[0]);
        int patientId = Integer.parseInt(patient.split(" - ")[0]);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, doctorId);
            pst.setInt(2, patientId);
            pst.setString(3, date);
            pst.setString(4, time);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                dateField.setText("");
                timeField.setText("");
                loadAppointments();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = 
                "SELECT a.appointment_id, d.name AS doctor, p.name AS patient, a.appointment_date, a.appointment_time " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN patients p ON a.patient_id = p.patient_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("appointment_id"),
                        rs.getString("doctor"),
                        rs.getString("patient"),
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
