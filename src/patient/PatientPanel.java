package patient;

import db.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class PatientPanel extends JFrame {
    private JTextField nameField, ageField;
    private JComboBox<String> genderBox;
    private JButton addButton, refreshButton, clearButton, appointmentButton;
    private JTable patientTable;
    private DefaultTableModel tableModel;

    public PatientPanel() {
        setTitle("MediBook – Patient Management");
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
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 245, 249));
        add(mainPanel);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 245, 249));
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel with card-like appearance
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 227), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Patient Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(250, 35));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField();
        ageField.setPreferredSize(new Dimension(100, 35));
        ageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(ageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setPreferredSize(new Dimension(150, 35));
        genderBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(genderBox, gbc);

        // Button panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
    addButton = new JButton("Add Patient");
    styleButton(addButton, new Color(76, 175, 80)); // Green
    refreshButton = new JButton("Refresh");
    styleButton(refreshButton, new Color(33, 150, 243)); // Blue
    clearButton = new JButton("Clear");
    styleButton(clearButton, new Color(239, 83, 80)); // Red
    appointmentButton = new JButton("Appointment");
    styleButton(appointmentButton, new Color(255, 193, 7)); // Amber

    buttonPanel.add(addButton);
    buttonPanel.add(refreshButton);
    buttonPanel.add(clearButton);
    buttonPanel.add(appointmentButton);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        tablePanel.setBackground(new Color(240, 245, 249));
        
        JLabel tableTitle = new JLabel("Patient Records");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Gender"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        patientTable = new JTable(tableModel);
        customizeTable(patientTable);
        
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Actions
        addButton.addActionListener(e -> addPatient());
        refreshButton.addActionListener(e -> loadPatients());
        clearButton.addActionListener(e -> clearForm());
        appointmentButton.addActionListener(e -> {
            new appointment.AppointmentPanel().setVisible(true);
            this.dispose();
        });

        loadPatients();
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
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void customizeTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Center-align ID and Age columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Age
        
        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                return c;
            }
        });
    }
    
    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        genderBox.setSelectedIndex(0);
        nameField.requestFocus();
    }

    private void addPatient() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();

        if (name.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all required fields", 
                "Incomplete Information", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid age (1-120)", 
                    "Invalid Age", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Check if patient already exists
                String checkSql = "SELECT 1 FROM patients WHERE name = ? AND age = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, name);
                checkStmt.setInt(2, age);
                
                if (checkStmt.executeQuery().next()) {
                    JOptionPane.showMessageDialog(this, 
                        "A patient with this name and age already exists", 
                        "Duplicate Patient", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String insertSql = "INSERT INTO patients (name, age, gender) VALUES (?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(insertSql);
                pst.setString(1, name);
                pst.setInt(2, age);
                pst.setString(3, gender);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Patient added successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadPatients();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Age must be a valid number", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error adding patient: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPatients() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                tableModel.setRowCount(0);
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT * FROM patients ORDER BY name";
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
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(PatientPanel.this, 
                            "Error loading patients", 
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
            new PatientPanel().setVisible(true);
        });
    }
}