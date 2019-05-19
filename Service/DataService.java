package Service;

import java.sql.*;

public class DataService {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/BrewTool";
    private static final String DB_USER = "dod";
    private static final String DB_PASS = "1";
    private Connection conn;
    private Statement stmt;

    public DataService() {
        openDatabaseConnection();
    }

    private void openDatabaseConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet queryDatabase(String sql) {
        try {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void updateDatabase(String sql) {
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
