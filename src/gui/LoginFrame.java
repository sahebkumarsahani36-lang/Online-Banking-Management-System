
package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.security.MessageDigest;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.util.Base64;

import connection.DBConnection;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("Online Banking Management System");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(11, 28, 43));
        mainPanel.setLayout(null);

        JLabel title = new JLabel("ONLINE BANKING");
        title.setBounds(100, 55, 300, 40);
        title.setForeground(new Color(50, 195, 205));
        title.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(title);

        JLabel subtitle = new JLabel("Management System");
        subtitle.setBounds(150, 100, 250, 25);
        subtitle.setForeground(Color.WHITE);
        mainPanel.add(subtitle);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setBounds(75, 170, 300, 25);
        emailLabel.setForeground(Color.WHITE);
        mainPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(75, 200, 330, 40);
        mainPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(75, 265, 300, 25);
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(75, 295, 330, 40);
        mainPanel.add(passwordField);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setBounds(75, 365, 330, 45);
        loginButton.setBackground(new Color(50, 195, 205));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(loginButton);

        JButton registerButton = new JButton("CREATE NEW ACCOUNT");
        registerButton.setBounds(75, 425, 330, 40);
        mainPanel.add(registerButton);

        loginButton.addActionListener(e -> loginUser());

        registerButton.addActionListener(e -> {
            RegisterFrame frame = new RegisterFrame();
            frame.setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    // Password Verification
    private boolean verifyPassword(String password, String storedHash)
            throws Exception {

        if (storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(":");

        if (parts.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                120000,
                256
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] actualHash =
                    factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(expectedHash, actualHash);

        } finally {
            spec.clearPassword();
        }
    }

    // Database Login
    private void loginUser() {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter email and password!"
            );
            return;
        }

        String sql = "SELECT user_id, full_name, password_hash, role "
                + "FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, email);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    String storedHash = rs.getString("password_hash");

                    if (verifyPassword(password, storedHash)) {

                        int userId = rs.getInt("user_id");
                        String name = rs.getString("full_name");

                        String role = rs.getString("role");

                        // Normalize role
                        role = (role == null)
                                ? ""
                                : role.trim().toUpperCase();

                        System.out.println("Login Email: " + email);
                        System.out.println("Login Role: " + role);

                        if ("ADMIN".equals(role)) {

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Admin Login Successful!\nWelcome " + name
                            );

                            AdminDashboard dashboard =
                                    new AdminDashboard();

                            dashboard.setVisible(true);
                            dispose();

                        } else if ("CUSTOMER".equals(role)) {

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Customer Login Successful!\nWelcome " + name
                            );

                            CustomerDashboard dashboard =
                                    new CustomerDashboard(userId, name);

                            dashboard.setVisible(true);
                            dispose();

                        } else {

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Invalid User Role: " + role
                            );
                        }

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Incorrect Password!"
                        );
                    }

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Email Address Not Found!"
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login Error: " + ex.getMessage()
            );

            ex.printStackTrace();
        }
    }
}
