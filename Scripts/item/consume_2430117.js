var status = -1;

var nReward = [
[1, 2435457, 1], 
[2, 2435457, 1], 
[3, 2435457, 1], 
[4, 2435457, 1], 
[5, 2435457, 1], 
[6, 2435457, 1], 
[7, 2435457, 1], 
];

var pList = [];
var nQr = 10000010;
var pQr = 10000011;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function start() {
    action (1, 0, 0);
}

function getGDate() {
    var it = cm.getPlayer().getClient().loadCharacters(0).iterator();
    while (it.hasNext()) {
        var cUser = it.next();
        var con = null;
        var ps = null;
        var rs = null;
        try {
            con = Packages.database.DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT characterid, quest, customData FROM questinfo");
            rs = ps.executeQuery();
            while (rs.next()) {
                var v1 = rs.getInt("characterid");
                var v2 = rs.getInt("quest");
                var v3 = rs.getString("customData");
                if (v1 != cUser.getId()) {
                    continue;
                }
                if (v2 != nQr) {
                    continue;
                }
                if (v3.contains("nGift_Date=" + date)) {
                    pList.push(cUser.getName());
                    return false;
                }
            }
        } catch (err) {
            err.printStackTrace(java.lang.System.err);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (err) {
                    err.printStackTrace(java.lang.System.err);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (err) {
                    err.printStackTrace(java.lang.System.err);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (err) {
                    err.printStackTrace(java.lang.System.err);
                }
            }
        }
    }
    return true;
}

function getGValue() {
    var nList = [];
    var nValue = 0;
    var it = cm.getPlayer().getClient().loadCharacters(0).iterator();
    while (it.hasNext()) {
        var cUser = it.next();
        var con = null;
        var ps = null;
        var rs = null;
        try {
            con = Packages.database.DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT characterid, quest, customData FROM questinfo");
            rs = ps.executeQuery();
            while (rs.next()) {
                var v1 = rs.getInt("characterid");
                var v2 = rs.getInt("quest");
                var v3 = rs.getString("customData");
                if (v1 != cUser.getId()) {
                    continue;
                }
                if (v2 != pQr) {
                    continue;
                }
                if (!v3.contains("nGift_Value=")) {
                    continue;
                }
                v3 = v3.replace("nGift_Value=","");
                nList.push(v3);
            }
        } catch (err) {
            err.printStackTrace(java.lang.System.err);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (err) {
                    err.printStackTrace(java.lang.System.err);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (err) {
                    err.printStackTrace(java.lang.System.err);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (err) {
                    err.printStackTrace(java.lang.System.err);
                }
            }
        }
    }
    if (nList.length > 0) {
        nList.sort().reverse();
        nValue = parseInt(nList[0]);
    }
    return parseInt(nValue);
}

function setGValueDate() {
    cm.getPlayer().updateOneInfoQuest(nQr, "nGift_Date", date);
    cm.getPlayer().updateOneInfoQuest(pQr, "nGift_Value", (getGValue() + 1) + "");
    cm.getPlayer().saveToDB(false, false);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "";
            say += "#Cgray#골드애플 교환권을 사용 시, 일주일 동안\r\n매일 골드 애플 10개를 획득 할 수 있습니다.";
            for (i = 0; i < nReward.length; i++) {
                var pCheck = ((nReward[i][0] > getGValue()) ? "#Cyellow#" : "#Cgreen#");
                var nCheck = ((nReward[i][0] < 10) ? "0" : "");
                say += "\r\n#L" + i + "#" + pCheck + "(" + nCheck + nReward[i][0] + "일) #d골드 애플을 수령한다.#k";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            var tCheck = (nReward[selection][0] > getGValue());
            if (!tCheck) {
                cm.sendNext("\r\n해당 #b#i" + nReward[selection][1] + "# #z" + nReward[selection][1] + "##k은 이미 수령하셨습니다.");
                return;
            }
            if (!getGDate()) {
                cm.sendNext("\r\n해당 #e#b" + cm.getPlayer().getClient().getAccountName() + "#k#n 계정의 #e#b" + pList + "#k#n 캐릭터로 이미 참여를 완료하였습니다.");
                return;
            }
            if (nReward[selection][0] != (getGValue() + 1)) {
                cm.sendNext("\r\n해당 #d#i" + nReward[selection][1] + "# #z" + nReward[selection][1] + "# " + nReward[selection][2] + "개#k는 #e#r" + (nReward[selection][0] - 1) + "일차#k#n 보상을 수령 후에 획득 할 수 있는 보상 아이템입니다.");
                return;
            }
            if (cm.getInventory(2).getNumFreeSlot() < 1) {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            var tRand = Packages.server.Randomizer.rand(10, 11);
            var say = "해당 #e#b" + nReward[selection][0] + "일차#k#n 보상을 성공적으로 수령하였습니다.";
            say += "\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#d#i" + nReward[selection][1] + "# #z" + nReward[selection][1] + "# " + tRand + "개#k";
            cm.sendNext("\r\n" + say);
            setGValueDate();
            cm.gainItem(nReward[selection][1], tRand);
            break;
        }
    }
}