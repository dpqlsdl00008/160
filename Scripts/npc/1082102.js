var status = -1;

var value = 10200000;
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
            cm.sendYesNoS("#r수상한 바다#k로 입장하시겠습니까? 위험한 일이 생길 수도 있습니다.", 4);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(120041900) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 유저가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "goldbeach_enter").equals("3") && cm.getPlayer().getOneInfoQuest(value, "goldbeach_date").equals(date)) {
                cm.sendNext("\r\n수상한 바다는 #e#r1일 3회#k#n 입장이 가능합니다. ");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "goldbeach_date").equals(date) == false) {
                cm.getPlayer().updateOneInfoQuest(value, "goldbeach_enter", "1");
                cm.getPlayer().updateOneInfoQuest(value, "goldbeach_date", date);
            } else {
                var v5 = parseInt(cm.getPlayer().getOneInfoQuest(value, "goldbeach_enter"));
                cm.getPlayer().updateOneInfoQuest(value, "goldbeach_enter", (v5 + 1) + "");
            }
            var v6 = parseInt(cm.getPlayer().getOneInfoQuest(value, "goldbeach_enter"));
            cm.getPlayer().dropMessage(5, "오늘 수상한 바다 입장 횟수는 총 " + v6 + "회입니다. 앞으로 " + (3 - v6) + "회 더 입장 할 수 있습니다.");
            cm.resetMap(120041900);
            cm.warp(120041900, "out00");
            break;
        }
    }
}