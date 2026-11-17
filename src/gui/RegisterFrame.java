
package gui;

import connection.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterFrame() {

        setTitle("Customer Registration - Online Banking");
        setSize(550, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(11, 28, 43));
        mainPanel.setLayout(null);

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setBounds(130, 30, 350, 40);
        title.setForeground(new Color(50, 195, 205));
        title.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(title);

        JLabel subtitle = new JLabel("Online Banking Management System");
        subtitle.setBounds(125, 75, 350, 25);
        subtitle.setForeground(Color.WHITE);
        mainPanel.add(subtitle);

        addLabel(mainPanel, "Full Name", 110);
        nameField = addTextField(mainPanel, 140);

        addLabel(mainPanel, "Email Address", 205);
        emailField = addTextField(mainPanel, 235);

        addLabel(mainPanel, "Phone Number", 300);
        phoneField = addTextField(mainPanel, 330);

        addLabel(mainPanel, "Password", 395);
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 425, 350, 38);
        mainPanel.add(passwordField);

        addLabel(mainPanel, "Confirm Password", 475);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(100, 505, 350, 38);
        mainPanel.add(confirmPasswordField);

        JButton registerButton = new JButton("CREATE ACCOUNT");
        registerButton.setBounds(100, 560, 350, 40);
        registerButton.setBackground(new Color(50, 195, 205));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        mainPanel.add(registerButton);

        JButton backButton = new JButton("BACK TO LOGIN");
        backButton.setBounds(100, 610, 350, 30);
        mainPanel.add(backButton);

        registerButton.addActionListener(e -> registerUser());

        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private void addLabel(JPanel panel, String text, int y) {

        JLabel label = new JLabel(text);
        label.setBounds(100, y, 350, 25);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(label);
    }

    private JTextField addTextField(JPanel panel, int y) {

        JTextField field = new JTextField();
        field.setBounds(100, y, 350, 38);
        panel.add(field);

        return field;
    }

    // Password hashing method
    private String hashPassword(String password) throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                120000,
                256
        );

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    private void registerUser() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        String password = new String(passwordField.getPassword());
        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty()
                || phone.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill all fields!"
            );
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            JOptionPane.showMessageDialog(
                    this, "Please enter a valid email!"
            );
            return;
        }

        if (!phone.matches("\\d{10}")) {

            JOptionPane.showMessageDialog(
                    this, "Phone number must contain 10 digits!"
            );
            return;
        }

        if (password.length() < 8) {

            JOptionPane.showMessageDialog(
                    this, "Password must be at least 8 characters!"
            );
            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this, "Passwords do not match!"
            );
            return;
        }

        String sql = "INSERT INTO users "
                + "(full_name, email, password_hash, phone) "
                + "VALUES (?, ?, ?, ?)";

        try {

            String hashedPassword = hashPassword(password);

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, name);
                pst.setString(2, email);
                pst.setString(3, hashedPassword);
                pst.setString(4, phone);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Registration Successful!\n"
                                + "Your account has been created."
                );

                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");

            }

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {

                JOptionPane.showMessageDialog(
                        this,
                        "This email is already registered!"
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Database Error: " + e.getMessage()
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password processing error: " + e.getMessage()
            );
        }
    }
}
