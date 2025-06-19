package Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB {
    private static Connection con;
    PreparedStatement pst;

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/fasttrack_logistics", "root", "");
            System.out.println("Connection Success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new RuntimeException("Database connection failed: " + e1.getMessage());
        }
        return con;
    }



}
