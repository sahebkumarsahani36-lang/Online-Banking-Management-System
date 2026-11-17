
package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnection {

    public static Connection getConnection()
            throws SQLException {

        Properties properties = new Properties();

        try (InputStream input =
                     DBConnection.class.getClassLoader()
                             .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new SQLException("db.properties file not found");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new SQLException("Cannot read database configuration", e);
        }

        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        return DriverManager.getConnection(url, user, password);
    }
}
