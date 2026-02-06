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
            cm.sendSimple("이 곳에 암벽 거인의 타락을 주재하려는 자가 있네. 어떻게 하겠는가?\r\n#L0##b1. 타란튤로스 타도 (파티(1~3인)만 입장 가능)#k\r\n#L1##b2. 오늘 남은 입장 횟수를 확인한다.#k");
            break;
        }
        case 1: {
            if (selection == 1) {
                cm.sendNext("\r\n자네는 현재 " + (5 - parseInt(cm.getPlayer().getOneInfoQuest(value, "colossus_enter"))) + "번 입장이 가능하다네.");
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
            if (cm.getPlayerCount(240093300) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            var pMember = cm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser.getOneInfoQuest(value, "colossus_enter").equals("5") && ccUser.getOneInfoQuest(value, "colossus_date").equals(date)) {
                        cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 타란튤로스 타도는 #b1일 5회#k 입장이 가능합니다.");
                        return;
                    }
                    if (ccUser.getOneInfoQuest(value, "colossus_date").equals(date) == false) {
                        ccUser.updateOneInfoQuest(value, "colossus_enter", "1");
                        ccUser.updateOneInfoQuest(value, "colossus_date", date);
                    } else {
                        var v5 = parseInt(ccUser.getOneInfoQuest(value, "colossus_enter"));
                        ccUser.updateOneInfoQuest(value, "colossus_enter", (v5 + 1) + "");
                    }
                }
            }
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        var v5 = parseInt(ccUser.getOneInfoQuest(value, "colossus_enter"));
                        ccUser.dropMessage(5, "암벽 거인의 심장으로 입장합니다. (" + v5 + "/5)");
                    }
                }
            }
            cm.resetMap(240093300);
            cm.warpParty(240093300);
            break;
        }
    }
}