
package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import connection.DBConnection;

public class CustomerDashboard extends JFrame {

    private int userId;
    private String customerName;

    private JLabel balanceLabel;
    private JLabel accountLabel;

    public CustomerDashboard(int userId, String customerName) {

        this.userId = userId;
        this.customerName = customerName;

        setTitle("Online Banking - Customer Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(11, 28, 43));
        header.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel title = new JLabel("ONLINE BANKING");
        title.setForeground(new Color(50, 195, 205));
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel welcome = new JLabel("Welcome, " + customerName);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Arial", Font.BOLD, 16));

        header.add(title, BorderLayout.WEST);
        header.add(welcome, BorderLayout.EAST);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(11, 28, 43));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new GridLayout(6, 1, 5, 10));

        String[] menu = {
                "Dashboard",
                "My Account",
                "Money Transfer",
                "Transactions",
                "My Profile",
                "Logout"
        };

        for (String item : menu) {

            JButton button = new JButton(item);
            button.setBackground(new Color(50, 195, 205));
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Arial", Font.BOLD, 13));

            sidebar.add(button);

            button.addActionListener(e -> {

                if (item.equals("Money Transfer")) {

                    TransferFrame frame =
                            new TransferFrame(userId, customerName);

                    frame.setVisible(true);

                } else if (item.equals("Transactions")) {

                    TransactionHistoryFrame frame =
                            new TransactionHistoryFrame(userId);

                    frame.setVisible(true);

                } else if (item.equals("My Profile")) {

                    CustomerProfileFrame frame =
                            new CustomerProfileFrame(userId);

                    frame.setVisible(true);

                } else if (item.equals("Logout")) {

                    new LoginFrame().setVisible(true);
                    dispose();

                } else if (item.equals("Dashboard")) {

                    loadAccountDetails();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            item + " feature will be added next."
                    );
                }
            });
        }

        // Main Content
        JPanel content = new JPanel();
        content.setBackground(new Color(235, 242, 245));
        content.setLayout(null);

        JLabel heading = new JLabel("Customer Dashboard");
        heading.setBounds(35, 25, 400, 40);
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(11, 28, 43));

        content.add(heading);

        JPanel balanceCard = new JPanel(null);
        balanceCard.setBounds(35, 100, 280, 170);
        balanceCard.setBackground(new Color(11, 28, 43));

        JLabel balanceTitle = new JLabel("ACCOUNT BALANCE");
        balanceTitle.setBounds(20, 20, 240, 30);
        balanceTitle.setForeground(Color.WHITE);
        balanceTitle.setFont(new Font("Arial", Font.BOLD, 16));

        balanceCard.add(balanceTitle);

        balanceLabel = new JLabel("Loading...");
        balanceLabel.setBounds(20, 70, 240, 45);
        balanceLabel.setForeground(new Color(50, 195, 205));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 25));

        balanceCard.add(balanceLabel);

        accountLabel = new JLabel("Account: Loading...");
        accountLabel.setBounds(20, 125, 250, 25);
        accountLabel.setForeground(Color.WHITE);

        balanceCard.add(accountLabel);

        content.add(balanceCard);

        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        loadAccountDetails();
    }

    private void loadAccountDetails() {

        String sql = "SELECT account_number, balance "
                + "FROM accounts WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    balanceLabel.setText(
                            "₹ " + rs.getBigDecimal("balance")
                    );

                    accountLabel.setText(
                            "Account: " + rs.getString("account_number")
                    );

                } else {

                    balanceLabel.setText("Not Available");
                    accountLabel.setText("No account linked yet");
                }
            }

        } catch (SQLException e) {

            balanceLabel.setText("Database Error");
            accountLabel.setText("Could not load account");

            e.printStackTrace();
        }
    }
}
