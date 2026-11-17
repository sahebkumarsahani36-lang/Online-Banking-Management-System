
package gui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import connection.DBConnection;

public class TransferFrame extends JFrame {

    private int userId;
    private String customerName;

    private JLabel senderAccountLabel;
    private JTextField receiverField;
    private JTextField amountField;

    public TransferFrame(int userId, String customerName) {

        this.userId = userId;
        this.customerName = customerName;

        setTitle("Online Banking - Money Transfer");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(11, 28, 43));

        JLabel title = new JLabel("MONEY TRANSFER");
        title.setBounds(100, 35, 320, 40);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(50, 195, 205));
        panel.add(title);

        JLabel welcome = new JLabel("Customer: " + customerName);
        welcome.setBounds(50, 90, 350, 25);
        welcome.setForeground(Color.WHITE);
        panel.add(welcome);

        JLabel senderLabel = new JLabel("Your Account Number");
        senderLabel.setBounds(50, 135, 350, 25);
        senderLabel.setForeground(Color.WHITE);
        panel.add(senderLabel);

        senderAccountLabel = new JLabel("Loading...");
        senderAccountLabel.setBounds(50, 160, 350, 30);
        senderAccountLabel.setForeground(new Color(50, 195, 205));
        senderAccountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(senderAccountLabel);

        JLabel receiverLabel = new JLabel("Receiver Account Number");
        receiverLabel.setBounds(50, 210, 350, 25);
        receiverLabel.setForeground(Color.WHITE);
        panel.add(receiverLabel);

        receiverField = new JTextField();
        receiverField.setBounds(50, 238, 380, 35);
        panel.add(receiverField);

        JLabel amountLabel = new JLabel("Transfer Amount (₹)");
        amountLabel.setBounds(50, 285, 350, 25);
        amountLabel.setForeground(Color.WHITE);
        panel.add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(50, 312, 380, 35);
        panel.add(amountField);

        JButton transferButton = new JButton("TRANSFER MONEY");
        transferButton.setBounds(50, 375, 380, 40);
        transferButton.setBackground(new Color(50, 195, 205));
        transferButton.setForeground(Color.BLACK);
        transferButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(transferButton);

        JButton closeButton = new JButton("CLOSE");
        closeButton.setBounds(50, 425, 380, 30);
        panel.add(closeButton);

        transferButton.addActionListener(e -> transferMoney());

        closeButton.addActionListener(e -> dispose());

        add(panel);

        loadSenderAccount();
    }

    private void loadSenderAccount() {

        String sql = "SELECT account_number FROM accounts "
                + "WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    senderAccountLabel.setText(
                            rs.getString("account_number")
                    );
                } else {
                    senderAccountLabel.setText("No active account found");
                }
            }

        } catch (SQLException e) {
            senderAccountLabel.setText("Database Error");
            e.printStackTrace();
        }
    }

    private static class Account {

        int id;
        String number;
        BigDecimal balance;

        Account(int id, String number, BigDecimal balance) {
            this.id = id;
            this.number = number;
            this.balance = balance;
        }
    }

    private Account getSenderAccount(Connection con)
            throws SQLException {

        String sql = "SELECT account_id, account_number, balance "
                + "FROM accounts "
                + "WHERE user_id = ? AND status = 'ACTIVE' "
                + "FOR UPDATE";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    return new Account(
                            rs.getInt("account_id"),
                            rs.getString("account_number"),
                            rs.getBigDecimal("balance")
                    );
                }
            }
        }

        return null;
    }

    private Account getReceiverAccount(Connection con, String number)
            throws SQLException {

        String sql = "SELECT account_id, account_number, balance "
                + "FROM accounts "
                + "WHERE account_number = ? AND status = 'ACTIVE' "
                + "FOR UPDATE";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, number);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    return new Account(
                            rs.getInt("account_id"),
                            rs.getString("account_number"),
                            rs.getBigDecimal("balance")
                    );
                }
            }
        }

        return null;
    }

    private void transferMoney() {

        String receiverNumber = receiverField.getText().trim();
        String amountText = amountField.getText().trim();

        if (receiverNumber.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please fill all fields!"
            );
            return;
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountText);

            if (amount.compareTo(BigDecimal.ZERO) <= 0
                    || amount.scale() > 2) {
                JOptionPane.showMessageDialog(
                        this, "Enter a valid amount greater than zero "
                                + "with maximum 2 decimal places."
                );
                return;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Please enter a valid amount!"
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Transfer ₹" + amount + " to account "
                        + receiverNumber + "?",
                "Confirm Demo Transfer",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                Account sender = getSenderAccount(con);

                if (sender == null) {
                    throw new SQLException("Your active account was not found.");
                }

                Account receiver = getReceiverAccount(con, receiverNumber);

                if (receiver == null) {
                    throw new SQLException("Receiver account not found or inactive.");
                }

                if (sender.id == receiver.id) {
                    throw new SQLException("You cannot transfer to your own account.");
                }

                if (sender.balance.compareTo(amount) < 0) {
                    throw new SQLException("Insufficient account balance.");
                }

                String updateSql =
                        "UPDATE accounts SET balance = ? WHERE account_id = ?";

                try (PreparedStatement pst =
                             con.prepareStatement(updateSql)) {

                    pst.setBigDecimal(
                            1, sender.balance.subtract(amount)
                    );
                    pst.setInt(2, sender.id);
                    pst.executeUpdate();

                    pst.setBigDecimal(
                            1, receiver.balance.add(amount)
                    );
                    pst.setInt(2, receiver.id);
                    pst.executeUpdate();
                }

                String insertSql =
                        "INSERT INTO transactions "
                                + "(account_id, transaction_type, amount, description) "
                                + "VALUES (?, ?, ?, ?)";

                try (PreparedStatement pst =
                             con.prepareStatement(insertSql)) {

                    pst.setInt(1, sender.id);
                    pst.setString(2, "DEBIT");
                    pst.setBigDecimal(3, amount);
                    pst.setString(
                            4, "Demo transfer to " + receiver.number
                    );
                    pst.executeUpdate();

                    pst.setInt(1, receiver.id);
                    pst.setString(2, "CREDIT");
                    pst.setBigDecimal(3, amount);
                    pst.setString(
                            4, "Demo transfer from " + sender.number
                    );
                    pst.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Demo Transfer Successful!\n"
                                + "Amount: ₹" + amount + "\n"
                                + "Receiver: " + receiver.number
                );

                amountField.setText("");
                receiverField.setText("");

            } catch (Exception e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Transfer Failed: " + e.getMessage()
                );
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
