package Database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionBuild {
    String url = "jdbc:sqlserver://localhost:1433;databaseName=Project_Manager;encrypt=true;trustServerCertificate=true;";
    String user = "sa";
    String passwrod = "12345";
    public Connection conn;


    public Connection buildConnection() {
        try {
            conn = DriverManager.getConnection(url, user, passwrod);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
