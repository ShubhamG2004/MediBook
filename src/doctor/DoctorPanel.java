package doctor;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DoctorPanel extends JFrame {
    private JTextField nameField, specializationField;
    private JButton addButton, refreshButton, deleteButton, updateButton;
    private JTable doctorTable;
    private DefaultTableModel tableModel;

    public DoctorPanel() {
        setTitle("MediBook – Doctor Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Doctor Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        formPanel.setBackground(Color.WHITE);

        // Name field
        formPanel.add(new JLabel("Doctor Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        // Specialization field
        formPanel.add(new JLabel("Specialization:"));
        specializationField = new JTextField();
        formPanel.add(specializationField);

        // Button panel - using FlowLayout for simplicity
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        addButton = new JButton("Add Doctor");
        styleButton(addButton, new Color(46, 204, 113));
        addButton.addActionListener(this::addDoctor);
        
        updateButton = new JButton("Update");
        styleButton(updateButton, new Color(52, 152, 219));
        updateButton.addActionListener(this::updateDoctor);
        updateButton.setEnabled(false);
        
        deleteButton = new JButton("Delete");
        styleButton(deleteButton, new Color(231, 76, 60));
        deleteButton.addActionListener(this::deleteDoctor);
        deleteButton.setEnabled(false);
        
        refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(149, 165, 166));
        refreshButton.addActionListener(e -> loadDoctors());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        formPanel.add(new JLabel()); // Empty label for spacing
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tablePanel.setBackground(new Color(245, 245, 245));

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Specialization"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        doctorTable.setRowHeight(25);
        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = doctorTable.getSelectedRow() != -1;
            deleteButton.setEnabled(rowSelected);
            updateButton.setEnabled(rowSelected);
            
            if (rowSelected) {
                int selectedRow = doctorTable.getSelectedRow();
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                specializationField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });
        
        // Style table header
        JTableHeader header = doctorTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadDoctors();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void addDoctor(ActionEvent e) {
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();

        if (name.isEmpty() || specialization.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", 
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO doctors (name, specialization) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, name);
            pst.setString(2, specialization);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Doctor added successfully!");
                resetForm();
                loadDoctors();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error adding doctor: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDoctor(ActionEvent e) {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) return;

        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE doctors SET name = ?, specialization = ? WHERE doctor_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, specialization);
            pst.setInt(3, doctorId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Doctor updated successfully!");
                resetForm();
                loadDoctors();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error updating doctor: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDoctor(ActionEvent e) {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this doctor?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM doctors WHERE doctor_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, doctorId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!");
                loadDoctors();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error deleting doctor: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDoctors() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM doctors ORDER BY doctor_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("doctor_id"),
                    rs.getString("name"),
                    rs.getString("specialization")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading doctors: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
    }

    private void resetForm() {
        nameField.setText("");
        specializationField.setText("");
        doctorTable.clearSelection();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            DoctorPanel frame = new DoctorPanel();
            frame.setVisible(true);
        });
    }
}