function start() {
    var text = "#e★ 인기도 랭킹 Top 10 ★#n#k\r\n\r\n";

    try {
        var conn = Packages.database.DatabaseConnection.getConnection();

        // 상위 10명 출력
        var ps = conn.prepareStatement("SELECT name, fame FROM characters WHERE gm = 0 ORDER BY fame DESC LIMIT 10");
        var rs = ps.executeQuery();

        var rank = 1;
        while (rs.next()) {
            var name = rs.getString("name");
            var fame = rs.getInt("fame");

            if (rank == 1) {
                text += "#e#r" + rank + "위#n#k : "
                     + "#e#Cyellow#" + name + "#n#k "
                     + "(인기도: #Cyellow#" + fame + "#k)\r\n";
            } else if (rank == 2) {
                text += "#e#r" + rank + "위#n#k : "
                     + "#e#g" + name + "#n#k "
                     + "(인기도: #g" + fame + "#k)\r\n";
            } else {
                text += "#r" + rank + "위#k : "
                     + "#b" + name + "#k "
                     + "(인기도: " + fame + ")\r\n";
            }
            rank++;
        }

        rs.close();
        ps.close();

        // 인기도 음수 유저 표시
        text += "\r\n#e#r▼ 비매너 유저 ▼#n#k\r\n\r\n";

        var ps2 = conn.prepareStatement("SELECT name, fame FROM characters WHERE gm = 0 AND fame < 0 ORDER BY fame ASC LIMIT 20");
        var rs2 = ps2.executeQuery();

        var badRank = 1;
        while (rs2.next()) {
            var name = rs2.getString("name");
            var fame = rs2.getInt("fame");

            text += "#e#r" + badRank + "위#k : "
                 + "#d" + name + "#k "
                 + "(인기도: " + fame + ")\r\n";

            badRank++;
        }

        rs2.close();
        ps2.close();

    } catch (e) {
        text = "랭킹을 불러오는 중 오류 발생:\r\n" + e;
    }

    cm.sendOk(text);
    cm.dispose();
}