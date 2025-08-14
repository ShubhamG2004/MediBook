package appointment;

import db.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set application icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/medical-icon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Icon not found, using default");
        }

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 245, 249));
        add(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 245, 249));
        JLabel titleLabel = new JLabel("Appointment Booking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel with card-like appearance
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 227), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Select Doctor:"), gbc);
        gbc.gridx = 1;
        doctorBox = new JComboBox<>();
        doctorBox.setPreferredSize(new Dimension(250, 30));
        formPanel.add(doctorBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Select Patient:"), gbc);
        gbc.gridx = 1;
        patientBox = new JComboBox<>();
        patientBox.setPreferredSize(new Dimension(250, 30));
        formPanel.add(patientBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField();
        dateField.setPreferredSize(new Dimension(250, 30));
        formPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1;
        timeField = new JTextField();
        timeField.setPreferredSize(new Dimension(250, 30));
        formPanel.add(timeField, gbc);

        // Button panel
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        bookButton = new JButton("Book Appointment");
        styleButton(bookButton, new Color(76, 175, 80));
        refreshButton = new JButton("Refresh List");
        styleButton(refreshButton, new Color(33, 150, 243));
        
        buttonPanel.add(bookButton);
        buttonPanel.add(refreshButton);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        tablePanel.setBackground(new Color(240, 245, 249));
        
        JLabel tableTitle = new JLabel("Scheduled Appointments");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        tableModel = new DefaultTableModel(new String[]{"ID", "Doctor", "Patient", "Date", "Time"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setRowHeight(30);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        appointmentTable.setShowGrid(false);
        appointmentTable.setIntercellSpacing(new Dimension(0, 0));
        
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Actions
        bookButton.addActionListener(e -> bookAppointment());
        refreshButton.addActionListener(e -> loadAppointments());

        loadDoctors();
        loadPatients();
        loadAppointments();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadDoctors() {
        doctorBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT doctor_id, name FROM doctors ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                doctorBox.addItem(rs.getInt("doctor_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading doctors", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPatients() {
        patientBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT patient_id, name FROM patients ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                patientBox.addItem(rs.getInt("patient_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading patients", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookAppointment() {
        String doctor = (String) doctorBox.getSelectedItem();
        String patient = (String) patientBox.getSelectedItem();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();

        if (doctor == null || patient == null || date.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields", 
                "Incomplete Information", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = Integer.parseInt(doctor.split(" - ")[0]);
        int patientId = Integer.parseInt(patient.split(" - ")[0]);

        try (Connection conn = DBConnection.getConnection()) {
            // Check for duplicate appointment
            String checkSql = "SELECT 1 FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, doctorId);
            checkStmt.setString(2, date);
            checkStmt.setString(3, time);
            
            if (checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, 
                    "This doctor already has an appointment at the selected time", 
                    "Conflict", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String insertSql = "INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(insertSql);
            pst.setInt(1, doctorId);
            pst.setInt(2, patientId);
            pst.setString(3, date);
            pst.setString(4, time);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Appointment booked successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dateField.setText("");
                timeField.setText("");
                loadAppointments();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error booking appointment: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAppointments() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                tableModel.setRowCount(0);
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = 
                        "SELECT a.appointment_id, d.name AS doctor, p.name AS patient, " +
                        "a.appointment_date, a.appointment_time " +
                        "FROM appointments a " +
                        "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                        "JOIN patients p ON a.patient_id = p.patient_id " +
                        "ORDER BY a.appointment_date, a.appointment_time";
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
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(AppointmentPanel.this, 
                            "Error loading appointments", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AppointmentPanel().setVisible(true);
        });
    }
}