
package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import connection.DBConnection;

public class AdminTransactionHistoryFrame extends JFrame {

    private JTable transactionTable;
    private DefaultTableModel model;

    public AdminTransactionHistoryFrame() {

        setTitle("Admin - All Transactions");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("ALL CUSTOMER TRANSACTIONS");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setHorizontalAlignment(SwingConstants.CENTER);

        String[] columns = {
                "Transaction ID",
                "Customer Name",
                "Account Number",
                "Type",
                "Amount",
                "Description",
                "Date & Time"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(model);
        transactionTable.setRowHeight(25);
        transactionTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(transactionTable);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTransactions());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadTransactions();
    }

    private void loadTransactions() {

        model.setRowCount(0);

        String sql =
                "SELECT t.transaction_id, u.full_name, " +
                        "a.account_number, t.transaction_type, " +
                        "t.amount, t.description, t.transaction_date " +
                        "FROM transactions t " +
                        "JOIN accounts a ON t.account_id = a.account_id " +
                        "JOIN users u ON a.user_id = u.user_id " +
                        "ORDER BY t.transaction_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                Object[] row = {
                        rs.getInt("transaction_id"),
                        rs.getString("full_name"),
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"),
                        rs.getTimestamp("transaction_date")
                };

                model.addRow(row);
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this, "No transactions found."
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading transactions: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}
