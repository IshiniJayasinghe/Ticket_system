package cli;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OpenSQL {
    public Connection con;

    // Initialize connection
    public Connection initializeConnection() throws SQLException {
        try {
            if (con == null || con.isClosed()) {
                String password = "";
                String username = "root";
                String url = "jdbc:mysql://localhost:3306/ticket system?useSSL=false";
                con = DriverManager.getConnection(url, username, password);
                System.out.println("Database connection established.");
            }
        } catch (CommunicationsException e) {
            System.err.println("Error: Unable to communicate with the database. Check your connection settings.");
            throw new SQLException("Database not connected: CommunicationsException");
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw new SQLException("Database not connected: SQLException");
        } catch (RuntimeException e){
            throw new RuntimeException("Connection error");
        }
        return con;
    }

    // Close connection
    public void BreakConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            throw new RuntimeException(e); // Re-throw if something critical happens
        }
    }

    public Connection getConnection() {
        return con;
    }
}