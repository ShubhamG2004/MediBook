package auth;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("MediBook – Hospital Appointment Booking System");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Login successful as " + role);

                // Role-based redirection (we will create these later)
                if (role.equals("doctor") || role.equals("admin")) {
                    new doctor.DoctorPanel().setVisible(true);
                }
                else if (role.equals("patient")) {
                    new patient.PatientPanel().setVisible(true);
                }
                else {
                    System.out.println("Redirect to Admin Panel");
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
