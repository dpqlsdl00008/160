var status = -1;

var value = 20000000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendSimple("위험 지역! 크세르크세스가 있는 곳입니다. 입장하시겠습니까?#d\r\n#L0#1. 입장 한다.\r\n#L1#2. 되돌아 간다.");
            break;
        }
        case 1: {
            if (selection == 1) {
                cm.dispose();
                return;
            }
            cm.dispose();
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n파티장만이 입장을 신청 할 수 있습니다.");
                return;
            }
            if (cm.allMembersHere() == false) {
                cm.sendNext("\r\n모든 파티원이 모여야 입장을 진행 할 수 있습니다.");
                return;
            }
            if (cm.getPlayerCount(200101500) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            var pMember = cm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser.getOneInfoQuest(value, "kserkses_enter").equals("5") && ccUser.getOneInfoQuest(value, "kserkses_date").equals(date)) {
                        cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 격전 크세르크세스는 #b1일 5회#k 입장이 가능합니다.");
                        return;
                    }
                    if (ccUser.getOneInfoQuest(value, "kserkses_date").equals(date) == false) {
                        ccUser.updateOneInfoQuest(value, "kserkses_enter", "1");
                        ccUser.updateOneInfoQuest(value, "kserkses_date", date);
                    } else {
                        var v5 = parseInt(ccUser.getOneInfoQuest(value, "kserkses_enter"));
                        ccUser.updateOneInfoQuest(value, "kserkses_enter", (v5 + 1) + "");
                    }
                }
            }
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        var v5 = parseInt(ccUser.getOneInfoQuest(value, "kserkses_enter"));
                        ccUser.dropMessage(5, "격전 크세르크세스로 입장합니다. (" + v5 + "/5)");
                    }
                }
            }
            cm.resetMap(200101500);
            cm.spawnMobOnMap(6160003, 1, 445, 226, 200101500);
            cm.warpParty(200101500, "out00");
            break;
        }
    }
}