package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Main java file that is used to connect to the database
// We can simply fetch the connection from this static function
// Prevents any boilerplate code

public class DBconnect {
    private static final String URL = "jdbc:postgresql://localhost:5432/BankApp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aryan2593";
    private static Connection connection = null;

    private DBconnect(){}; // To prevent object creation


    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println(" Database connection failed: " + e.getMessage());
            }
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
