
package gui;

import connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminRoleSetup {

    public static void main(String[] args) {

        String email = "rahul@example.com";

        String updateSQL =
                "UPDATE users SET role = 'ADMIN' WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(updateSQL)) {

            pst.setString(1, email);

            int rows = pst.executeUpdate();

            if (rows > 0) {

                System.out.println("Admin role updated successfully!");

            } else {

                System.out.println("Email not found!");
                return;
            }

            String checkSQL =
                    "SELECT email, role FROM users WHERE email = ?";

            try (PreparedStatement check =
                         con.prepareStatement(checkSQL)) {

                check.setString(1, email);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next()) {
                        System.out.println("Email: " +
                                rs.getString("email"));

                        System.out.println("Role: " +
                                rs.getString("role"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
