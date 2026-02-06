/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login.handler;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginAutoRegister {
    
    public static void registerAccount(String name, String password, String ip, boolean gender) {//true=여자,false=남자
        
        PreparedStatement query = null;
        try {
           Connection connect = DatabaseConnection.getConnection();
           query = connect.prepareStatement("INSERT INTO accounts (name, password, SessionIP, gender) VALUES (?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
           query.setString(1, name);
           query.setString(2, password);
           query.setString(3, ip);
           query.setInt(4, gender ? 1 : 0);
           query.executeUpdate();
           connect.close();
        } catch (Exception error) {
                System.err.println("[1] Register Exception Error : " + error);
        } finally {
            try {
                if (query != null) {
                    query.close();
                }
            } catch (Exception E) {
                System.err.println("[2] Register Exception Error : " + E);
            }
        }
    }

    public static boolean checkIP(String ip) {
        
        PreparedStatement query = null;
        ResultSet result = null;
        Connection connect = null;

        try {
           connect = DatabaseConnection.getConnection();
           query = connect.prepareStatement("SELECT SessionIP FROM accounts WHERE SessionIP = ?");
           query.setString(1, ip);
           result = query.executeQuery();
           if (result.getRow() < 3) {
               return true;
           }
        } catch (Exception error) {
                System.err.println("[1] Register Exception Error : " + error);
        } finally {
            try {
                if (query != null) {
                    query.close();
                }
                if (result != null) {
                    result.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception E) {
                System.err.println("[2] Register Exception Error : " + E);
            }
        }
        return false;
    }        
}
