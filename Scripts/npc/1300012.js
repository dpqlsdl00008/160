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
            if (cm.isQuestFinished(2330) == true && cm.haveItem(4032388) == false) {
                cm.dispose();
                cm.gainItem(4032388, 1);
                cm.sendNextS("\r\n이것은 #b결혼식장 열쇠#k! 이걸로 #b비올레타 공주#k가 갇혀 있는 걸혼식장으로 들어 갈 수 있게 되었군.", 2);
                return;
            }
            var say = "#b결혼식장 입구#k로 이동합니다. 어디로 이동하시겠습니까?#Cyellow#";
            say += "\r\n#L0#1. 페페킹 타도 (파티(1~6인)만 입장 가능)";
            say += "\r\n#L1#2. 비올레타 구출 (솔로만 가능)";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    if (cm.getParty() == null || cm.isLeader() == false) {
                        cm.sendNext("\r\n파티장만이 입장을 신청 할 수 있습니다.");
                        return;
                    }
                    if (cm.allMembersHere() == false) {
                        cm.sendNext("\r\n모든 파티원이 모여야 입장을 진행 할 수 있습니다.");
                        return;
                    }
                    if (cm.getPlayerCount(106021500) != 0) {
                        cm.sendNext("\r\n현재 접속 한 채널에서는 다른 파티가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                        return;
                    }
                    var pMember = cm.getParty().getMembers();
                    if (pMember != null) {
                        var it = pMember.iterator();
                        while (it.hasNext()) {
                            var cUser = it.next();
                            var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                            if (ccUser.getOneInfoQuest(value, "pepeking_enter").equals("10") && ccUser.getOneInfoQuest(value, "pepeking_date").equals(date)) {
                                cm.sendNext("\r\n파티원 중 #e#b" + ccUser.getName() + "#k#n 님이 일일 #r도전 횟수 제한 횟수를 초과#k했습니다. 페페킹 타도는 #b1일 10회#k 진행이 가능합니다.");
                                 return;
                             }
                             if (ccUser.getOneInfoQuest(value, "pepeking_date").equals(date) == false) {
                                 ccUser.updateOneInfoQuest(value, "pepeking_enter", "1");
                                 ccUser.updateOneInfoQuest(value, "pepeking_date", date);
                             } else {
                                 var v5 = parseInt(ccUser.getOneInfoQuest(value, "pepeking_enter"));
                                 ccUser.updateOneInfoQuest(value, "pepeking_enter", (v5 + 1) + "");
                             }
                             var v6 = parseInt(ccUser.getOneInfoQuest(value, "pepeking_enter"));
                             ccUser.dropMessage(5, "오늘 페페킹 타도를 진행 한 횟수는 총 " + v5 + "회입니다. 앞으로 " + (10 - v5) + "회 더 입장 할 수 있습니다.");
                         }
                    }
                    cm.resetMap(106021500);
                    cm.warpParty(106021500, "out01");
                    break;
                }
                case 1: {
                    cm.sendYesNo("결혼식장 열쇠를 사용해 결혼식장으로 입장 할 수 있습니다. 입장 하시겠습니까?");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.haveItem(4032388) == false) {
                cm.sendNext("\r\n#i4032388# #b#z4032388##k를 소지 후에 입장을 진행 할 수 있습니다.");
                return;
            }
            if (cm.getPlayerCount(106021600) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 유저가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "violeta_enter").equals("3") && cm.getPlayer().getOneInfoQuest(value, "violeta_date").equals(date)) {
                cm.sendNext("\r\n비올레타 구출은 #e#r1일 3회#k#n 도전이 가능합니다. ");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "violeta_date").equals(date) == false) {
                cm.getPlayer().updateOneInfoQuest(value, "violeta_enter", "1");
                cm.getPlayer().updateOneInfoQuest(value, "violeta_date", date);
            } else {
                var v5 = parseInt(cm.getPlayer().getOneInfoQuest(value, "violeta_enter"));
                cm.getPlayer().updateOneInfoQuest(value, "violeta_enter", (v5 + 1) + "");
            }
            var v6 = parseInt(cm.getPlayer().getOneInfoQuest(value, "violeta_enter"));
            cm.getPlayer().dropMessage(5, "오늘 비올레타 구출을 진행 한 횟수는 총 " + v6 + "회입니다. 앞으로 " + (3 - v6) + "회 더 입장 할 수 있습니다.");
            cm.resetMap(106021600);
            cm.warp(106021600, "left00");
            break;
        }
    }
}