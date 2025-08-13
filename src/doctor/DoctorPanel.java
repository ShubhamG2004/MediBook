package doctor;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DoctorPanel extends JFrame {
    private JTextField nameField, specializationField;
    private JButton addButton, refreshButton;
    private JTable doctorTable;
    private DefaultTableModel tableModel;

    public DoctorPanel() {
        setTitle("MediBook – Doctor Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top Panel for form inputs
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Doctor Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Specialization:"));
        specializationField = new JTextField();
        formPanel.add(specializationField);

        addButton = new JButton("Add Doctor");
        refreshButton = new JButton("Refresh List");
        formPanel.add(addButton);
        formPanel.add(refreshButton);

        // Table for displaying doctors
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Specialization"}, 0);
        doctorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(doctorTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> addDoctor());
        refreshButton.addActionListener(e -> loadDoctors());

        loadDoctors();
    }

    private void addDoctor() {
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();

        if (name.isEmpty() || specialization.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO doctors (name, specialization) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, specialization);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Doctor added successfully!");
                nameField.setText("");
                specializationField.setText("");
                loadDoctors();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadDoctors() {
        tableModel.setRowCount(0); // Clear existing data
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM doctors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialization")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
