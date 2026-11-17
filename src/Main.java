//import gui.LoginFrame;
//import  javax.swing.SwingUtilities;
//public class Main {
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                System.out.println("Opening Login Frame....");
//                LoginFrame frame = new LoginFrame();
//                frame.setVisible(true);
//                System.out.println("login frame opened!");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        });
//    }
//}


import connection.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import gui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            System.out.println("================================");
            System.out.println("ONLINE BANKING MANAGEMENT SYSTEM");
            System.out.println("================================");
            System.out.println("MySQL Connected Successfully!");

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }

        // Open Login Window
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
