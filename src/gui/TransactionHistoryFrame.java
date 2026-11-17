
package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import connection.DBConnection;

public class TransactionHistoryFrame extends JFrame {

    private int userId;
    private JTable transactionTable;
    private DefaultTableModel model;

    public TransactionHistoryFrame(int userId) {

        this.userId = userId;

        setTitle("Transaction History");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("TRANSACTION HISTORY");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(11, 28, 43));
        heading.setBorder(
                BorderFactory.createEmptyBorder(20, 15, 10, 15)
        );

        add(heading, BorderLayout.NORTH);

        String[] columns = {
                "Transaction ID",
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
        transactionTable.setRowHeight(30);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));

        transactionTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        JScrollPane scrollPane =
                new JScrollPane(transactionTable);

        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTransactions());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);

        add(bottomPanel, BorderLayout.SOUTH);

        loadTransactions();
    }

    private void loadTransactions() {

        model.setRowCount(0);

        String sql =
                "SELECT t.transaction_id, a.account_number, " +
                        "t.transaction_type, t.amount, t.description, " +
                        "t.transaction_date " +
                        "FROM transactions t " +
                        "JOIN accounts a ON t.account_id = a.account_id " +
                        "WHERE a.user_id = ? " +
                        "ORDER BY t.transaction_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Object[] row = {
                            rs.getInt("transaction_id"),
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
                            this,
                            "No transactions found."
                    );
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading transactions: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
