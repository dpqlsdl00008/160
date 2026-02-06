
package handling.world;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.TimerTask;

public class TraitDailyCheck extends TimerTask {

    public void run() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET todaycharm = 0, todaycraft = 0, todaycharisma = 0, todaywill = 0, todaysense = 0, todayinsight = 0, todayrep = 0");
            ps.execute();
            System.out.println("24시간이 지나 모든 캐릭터의 오늘의 전문기술/오늘의 명성도가 0으로 초기화 되었습니다.");
        } catch (SQLException e) {
            System.err.println("체인지에러..: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
