
package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import connection.DBConnection;

public class AdminDashboard extends JFrame {

    private JLabel customerCount;
    private JLabel accountCount;
    private JLabel transactionCount;

    private JTable customerTable;
    private DefaultTableModel model;

    public AdminDashboard() {

        setTitle("Online Banking - Admin Dashboard");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel header = new JPanel();
        header.setBackground(new Color(25, 45, 85));
        header.setPreferredSize(new Dimension(1000, 65));

        JLabel title = new JLabel("ONLINE BANKING - ADMIN DASHBOARD");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(title);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statsPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        customerCount = new JLabel("Customers: 0", SwingConstants.CENTER);
        accountCount = new JLabel("Accounts: 0", SwingConstants.CENTER);
        transactionCount = new JLabel("Transactions: 0", SwingConstants.CENTER);

        JLabel[] labels = {
                customerCount, accountCount, transactionCount
        };

        for (JLabel label : labels) {
            label.setFont(new Font("Arial", Font.BOLD, 18));
            label.setOpaque(true);
            label.setBackground(new Color(220, 235, 250));
            statsPanel.add(label);
        }

        String[] columns = {
                "User ID", "Full Name", "Email", "Phone", "Role"
        };

        model = new DefaultTableModel(columns, 0);
        customerTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(customerTable);

        JButton refreshButton = new JButton("Refresh");
        JButton transactionsButton = new JButton("View All Transactions");
        JButton logoutButton = new JButton("Logout");

        refreshButton.addActionListener(e -> loadDashboardData());

        transactionsButton.addActionListener(e -> {
            new AdminTransactionHistoryFrame().setVisible(true);
        });

        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(transactionsButton);
        buttonPanel.add(logoutButton);

        add(header, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(
                new JLabel("  CUSTOMER DETAILS"),
                BorderLayout.NORTH
        );

        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        loadDashboardData();
    }

    private void loadDashboardData() {

        String customerSQL =
                "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'";

        String accountSQL =
                "SELECT COUNT(*) FROM accounts";

        String transactionSQL =
                "SELECT COUNT(*) FROM transactions";

        String userSQL =
                "SELECT user_id, full_name, email, phone, role FROM users";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            try (ResultSet rs = st.executeQuery(customerSQL)) {
                if (rs.next()) {
                    customerCount.setText(
                            "Customers: " + rs.getInt(1)
                    );
                }
            }

            try (ResultSet rs = st.executeQuery(accountSQL)) {
                if (rs.next()) {
                    accountCount.setText(
                            "Accounts: " + rs.getInt(1)
                    );
                }
            }

            try (ResultSet rs = st.executeQuery(transactionSQL)) {
                if (rs.next()) {
                    transactionCount.setText(
                            "Transactions: " + rs.getInt(1)
                    );
                }
            }

            model.setRowCount(0);

            try (ResultSet rs = st.executeQuery(userSQL)) {

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role")
                    });
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
