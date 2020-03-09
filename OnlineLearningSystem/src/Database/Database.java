package Database;

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public class Database {

    private static String url = "jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk";
    private static String username = "philadelphia";
    private static String password = "y8liulbkqy";
    public static Connection connection;
    //private static PGSimpleDataSource ds;
    
    /**
     * Opens a connection between server and database.
     */
    public static void makeConnection(){
        try {
//        	ds = new PGSimpleDataSource();
//            ds.setURL(url);
//            ds.setUser(username);
//            ds.setPassword(password);
//            DataSource dataSource = ds;
//            connection = ds.getConnection();
            connection = DriverManager.getConnection(url, username, password);
            
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection between server and database.
     */
    public static void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
