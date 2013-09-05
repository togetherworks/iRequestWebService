
package com.ncsi.olr.webservice.utilities;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The DbUtilities class contains database related utilities used by the I-Request Web Service.
 * @author Paul Panjikaran
 * @version 1.0
 */

public class DBUtilities {
  /** IP address and port of the database server */
    public static String databaseHost = "";

    private static String DB_USERNAME = "";
    private static String DB_PASSWORD = "";

    public static CallableStatement cstmt = null;
    public static PreparedStatement pstmt = null;
    public static ResultSet rs = null;



    /** return a connection to the database specified in the url */
    public static Connection getConnection ()
                    throws Exception {
    	
    	//new OLRProperties();
        String db_driver = OLRProperties.getProperty("LOOKUP_DB_DRIVER");
        String db_ip =     OLRProperties.getProperty("LOOKUP_DB_IP");
        String db_port =   OLRProperties.getProperty("LOOKUP_DB_PORT");
        String db_name =   OLRProperties.getProperty("DB_NAME").toLowerCase();
        String db_url = db_driver + db_ip + ":" + db_port + ":" + db_name;

        DB_USERNAME = OLRProperties.getProperty("LOOKUP_DB_USERNAME");
        DB_PASSWORD = OLRProperties.getProperty("LOOKUP_DB_PASSWORD");

        DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());

        return DriverManager.getConnection (db_url, DB_USERNAME, DB_PASSWORD);
    }


    /** Disconnect from data source */
    public static void closeConnection(Connection con) {
        if ( con != null )
        try {
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(ex), null);
        }
    }

    /** close a PreparedStatement */
    public static void closePreparedStatement (PreparedStatement pstmt) {
        if ( pstmt != null )
            try {
                pstmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(ex), null);
            }
    }


    

    public static void closeResultSet () {
        closeResultSet(rs);
        rs = null;
    }

    public static void closeResultSet (ResultSet rs) {
        if ( rs != null )
        try {
            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(ex), null);
        }
    }
    
    public static void closeCallableStatement (CallableStatement cstmt) {
        if ( cstmt != null )
        try {
          cstmt.close();
        } catch (Exception ex) {
          ex.printStackTrace();
          ErrorNotification.sendErrorNotification("1402", IRequestWSUtilities.getStackTrace(ex), null);
        }
    }

      public static String replace (String string, String from, String to) {
        int position = 0;
        while ((position = string.indexOf (from, position)) != -1) {
            string = string.substring (0, position) + to +
                    string.substring (position + from.length ());
            position += to.length ();
        }
        return string;
    }

}
