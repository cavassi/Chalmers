package chalmers.tda367.testing;


/**
 * Import SQLite and uncomment the things in this class to make sure it works
 */

// sqlite

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Main {

    public static void connect() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        Connection conn = null;
        try {
            String url = "jdbc:sqlite:database.sqlite";
            
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    public static void main(String[] args) throws ClassNotFoundException {
        //connect();
    }
}