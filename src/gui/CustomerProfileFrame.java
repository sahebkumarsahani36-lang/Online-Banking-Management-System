
package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import connection.DBConnection;

public class CustomerProfileFrame extends JFrame {

    private int userId;

    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel roleLabel;

    public CustomerProfileFrame(int userId) {

        this.userId = userId;

        setTitle("My Customer Profile");
        setSize(550, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel heading = new JLabel(
                "MY PROFILE",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(50, 195, 205));
        heading.setBackground(new Color(11, 28, 43));
        heading.setOpaque(true);
        heading.setBorder(
                BorderFactory.createEmptyBorder(25, 10, 25, 10)
        );

        add(heading, BorderLayout.NORTH);

        // Profile Panel
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBackground(new Color(235, 242, 245));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );

        idLabel = new JLabel("Customer ID: Loading...");
        nameLabel = new JLabel("Full Name: Loading...");
        emailLabel = new JLabel("Email: Loading...");
        phoneLabel = new JLabel("Phone: Loading...");
        roleLabel = new JLabel("Role: Loading...");

        JLabel[] labels = {
                idLabel, nameLabel, emailLabel,
                phoneLabel, roleLabel
        };

        for (JLabel label : labels) {
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(new Color(11, 28, 43));
            panel.add(label);
        }

        add(panel, BorderLayout.CENTER);

        loadProfile();
    }

    private void loadProfile() {

        String sql =
                "SELECT user_id, full_name, email, phone, role " +
                        "FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    idLabel.setText(
                            "Customer ID: " + rs.getInt("user_id")
                    );

                    nameLabel.setText(
                            "Full Name: " + rs.getString("full_name")
                    );

                    emailLabel.setText(
                            "Email: " + rs.getString("email")
                    );

                    phoneLabel.setText(
                            "Phone: " + rs.getString("phone")
                    );

                    roleLabel.setText(
                            "Role: " + rs.getString("role")
                    );

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Customer profile not found."
                    );
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
