package connector;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class connectorWalker {

    public static void setAlive(String id, boolean alive) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET allowed = ? WHERE name = ?");
            ps.setInt(1, alive ? 1 : 0);
            ps.setString(2, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void setIP(String id, String ip) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET connecterIP = ? WHERE name = ?");
            ps.setString(1, ip);
            ps.setString(2, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void setBanned(String id, boolean banned, String reason) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE name = ?");
            ps.setBoolean(1, banned);
            ps.setString(2, reason);
            ps.setString(3, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int Login(String id, String pw) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("allowed") == 1) {
                    rs.close();
                    return 4; //이미 접속중
                }
                String password = rs.getString("password");
                int banned = rs.getInt("banned");
                if (password.equalsIgnoreCase(pw)) {
                    if (banned > 0) {
                        rs.close();
                        return 3; // 밴
                    }
                    return 1; //로그인 성공
                }
            } else {
                return 2; // 아이디 없음
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0; //아이디 비밀번호 안맞음
    }

    public static int AutoRegister(final String id, final String pw) {
        try {
            int register = 0;
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name= ?");
            ps.setString(1, id);
            final ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                final PreparedStatement ps2 = con.prepareStatement("INSERT INTO accounts (name, password) VALUES (?, ?)");
                ps2.setString(1, id);
                ps2.setString(2, pw);
                ps2.executeUpdate();
                ps2.close();
                return 1; //성공
            }
            rs.close();
            ps.close();
            con.close();
            return 0; //이미 아이디 있음
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }
}
