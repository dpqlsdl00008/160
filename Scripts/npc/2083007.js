var status = -1;

var value = 20000000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
var bName = "";

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    var bNumber = parseInt((cm.getMapId() - 240070000) / 100);
    switch (status) {
        case 0: {
            var add = (cm.getMapId() == 240070302 ? "으" : "");
            var say = "이 곳은 #Cgreen##m" + (cm.getMapId() + 1) + "##k" + add + "로 가는 길 입니다. 무엇을 원하십니까?";
            switch (cm.getMapId()) {
                case 240070202: {
                    bName = "베르가모트";
                    break;
                }
                case 240070302: {
                    bName = "듀나스";
                    break;
                }
                case 240070402: {
                    bName = "아우프헤벤";
                    break;
                }
                case 240070502: {
                    bName = "오베론";
                    break;
                }
                case 240070602: {
                    bName = "니벨룽겐";
                    break;
                }
            }
            say += "#Cyellow#\r\n#L0#" + bName + " 타도 (파티(1~3인)만 입장 가능)";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getParty() == null || cm.isLeader() == false) {
                cm.sendNext("\r\n파티장만이 입장을 신청 할 수 있습니다.");
                return;
            }
            if (cm.allMembersHere() == false) {
                cm.sendNext("\r\n모든 파티원이 모여야 입장을 진행 할 수 있습니다.");
                return;
            }
            if (cm.getPlayerCount((cm.getMapId() + 1)) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            var pMember = cm.getParty().getMembers();
            if (pMember != null) {
                if (pMember.size() > 3) {
                    cm.sendNext("\r\n" + bName + " 타도는 #e#r3인 이하의 파티#k#n만 입장을 신청 할 수 있습니다.");
                    return;
                }
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser.getOneInfoQuest(value, "neoCityBoss_" + bNumber + "_enter").equals("5") && ccUser.getOneInfoQuest(value, "neoCityBoss_" + bNumber + "_date").equals(date)) {
                        cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. " + bName + " 타도는 #b1일 10회#k 진행이 가능합니다.");
                        return;
                    }
                    if (ccUser.getOneInfoQuest(value, "neoCityBoss_" + bNumber + "_date").equals(date) == false) {
                        ccUser.updateOneInfoQuest(value, "neoCityBoss_" + bNumber + "_enter", "1");
                        ccUser.updateOneInfoQuest(value, "neoCityBoss_" + bNumber + "_date", date);
                    } else {
                        var v5 = parseInt(ccUser.getOneInfoQuest(value, "neoCityBoss_" + bNumber + "_enter"));
                        ccUser.updateOneInfoQuest(value, "neoCityBoss_" + bNumber + "_enter", (v5 + 1) + "");
                    }
                    var v6 = parseInt(ccUser.getOneInfoQuest(value, "neoCityBoss_" + bNumber + "_enter"));
                    ccUser.dropMessage(5, "오늘 " + bName + " 타도를 진행 한 횟수는 총 " + v5 + "회입니다. 앞으로 " + (5 - v5) + "회 더 입장 할 수 있습니다.");
                }
                cm.resetMap((cm.getMapId() + 1));
                cm.warpParty((cm.getMapId() + 1), 0);
                break;
            }
        }
    }
}