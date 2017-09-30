package db;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBUtilities {

    //Performs routine operations to create a new Connection object
    private static Connection createConn() throws ClassNotFoundException, SQLException {
        Connection conn = null;

        Properties props = new Properties();

        //Set properties for conn
        props.setProperty("user", Constants.DBuser);
        props.setProperty("password", Constants.DBpassword);

        //Try to load driver for DB communications
        try {
            Class.forName(Constants.DBdriver);
        } catch (ClassNotFoundException e1) {
            throw e1;
        }

        //Establish connection
        try {
            conn = DriverManager.getConnection(Constants.DBurl, props);
        } catch (SQLException e) {
            throw e;
        }

        //Return an open connection
        return conn;
    }

    //Performs routine operations for execute a query and generating a ResultSet
    public static ResultSet executeQuery(String query) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            conn = createConn();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        }

        conn.close();

        return rs;
    }

    public static int executeUpdate(String command) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        int rs = 0;
        Connection conn = null;

        try {
            conn = createConn();
            stmt = conn.createStatement();
            rs = stmt.executeUpdate(command);
        } catch (SQLException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        }

        conn.close();

        return rs;
    }
}
