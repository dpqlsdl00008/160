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
            cm.sendYesNoS("이곳은 #Cyellow#에피네아의 은신처#k입니다.\r\n허락 받지 못한 자에게는 매우 위험한 곳 입니다.\r\n그래도 입장 하시겠습니까?", 4);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(300030310) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 유저가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "epinea_enter").equals("5") && cm.getPlayer().getOneInfoQuest(value, "epinea_date").equals(date)) {
                cm.sendNext("\r\n여왕의 은신처는 #e#r1일 3회#k#n 입장이 가능합니다. ");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "epinea_date").equals(date) == false) {
                cm.getPlayer().updateOneInfoQuest(value, "epinea_enter", "1");
                cm.getPlayer().updateOneInfoQuest(value, "epinea_date", date);
            } else {
                var v5 = parseInt(cm.getPlayer().getOneInfoQuest(value, "epinea_enter"));
                cm.getPlayer().updateOneInfoQuest(value, "epinea_enter", (v5 + 1) + "");
            }
            var v6 = parseInt(cm.getPlayer().getOneInfoQuest(value, "epinea_enter"));
            cm.getPlayer().dropMessage(5, "오늘 여왕의 은신처 입장 횟수는 총 " + v6 + "회입니다. 앞으로 " + (5 - v6) + "회 더 입장 할 수 있습니다.");
            cm.resetMap(300030310);
            cm.warp(300030310, "out00");
            break;
        }
    }
}