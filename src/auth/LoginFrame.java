package auth;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("MediBook – Hospital Appointment Booking System");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(52, 152, 219);
                Color color2 = new Color(41, 128, 185);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel with logo and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Logo (placeholder - you can replace with actual logo)
        // JLabel logoLabel = new JLabel("", SwingConstants.CENTER);
        // logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
        // logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("MediBook Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Username field
        JPanel usernamePanel = new JPanel(new BorderLayout(5, 5));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        
        usernameField = new JTextField();
        styleTextField(usernameField, "Enter your username");
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        // Password field
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        passwordPanel.setOpaque(false);
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        
        passwordField = new JPasswordField();
        styleTextField(passwordField, "Enter your password");
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Login button
        loginButton = new JButton("LOGIN");
        styleButton(loginButton);
        loginButton.addActionListener(e -> login());
        
        // Add components to form panel
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(loginButton);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200, 100), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(255, 255, 255, 200));
        field.setForeground(Color.DARK_GRAY);
        
        // Placeholder functionality
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.DARK_GRAY);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(46, 204, 113));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(39, 174, 96));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 204, 113));
            }
        });
    }

    private void login() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        // Check for placeholder values
        if (username.equals("Enter your username") || password.equals("Enter your password") || 
            username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", 
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                
                // Success message with custom icon
                JOptionPane.showMessageDialog(this, 
                    "Login successful!\nWelcome, " + username + " (" + role + ")", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE,
                    UIManager.getIcon("OptionPane.informationIcon"));

                // Role-based redirection
                SwingUtilities.invokeLater(() -> {
                    if (role.equals("doctor") || role.equals("admin")) {
                        new doctor.DoctorPanel().setVisible(true);
                    }
                    else if (role.equals("patient")) {
                        new patient.PatientPanel().setVisible(true);
                    }
                    dispose();
                });
            } else {
                // Error message with custom icon
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database connection error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}