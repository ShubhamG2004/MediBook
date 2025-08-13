package patient;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PatientPanel extends JFrame {
    private JTextField nameField, ageField;
    private JComboBox<String> genderBox;
    private JButton addButton, refreshButton;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    public PatientPanel() {
        setTitle("MediBook – Patient Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Patient Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        formPanel.add(ageField);

        formPanel.add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderBox);

        addButton = new JButton("Add Patient");
        refreshButton = new JButton("Refresh List");
        formPanel.add(addButton);
        formPanel.add(refreshButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Gender"}, 0);
        patientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(patientTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        addButton.addActionListener(e -> addPatient());
        refreshButton.addActionListener(e -> loadPatients());

        loadPatients();
    }

    private void addPatient() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();

        if (name.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO patients (name, age, gender) VALUES (?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setInt(2, age);
                pst.setString(3, gender);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Patient added successfully!");
                    nameField.setText("");
                    ageField.setText("");
                    genderBox.setSelectedIndex(0);
                    loadPatients();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadPatients() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM patients";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
