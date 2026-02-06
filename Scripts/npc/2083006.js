var status = -1;

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
    switch (status) {
        case 0: {
            var say = "";
            say += "#0#2021년 평범한 마을";
            say += "#1#2099년 한밤의 항만";
            say += "#2#2215년 폭격 맞은 도심";
            say += "#3#2216년 폐허가 된 도심";
            say += "#4#2230년 위험한 타워";
            say += "#5#2503년 천공전함 헤르메스호";
            cm.askMapSelection(say);
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.isQuestActive(3766) == true && cm.isQuestFinished(3773) == false) {
                if (selection == 1) {
                    if (cm.getPlayerCount(240070210) != 0) {
                        cm.getPlayer().dropMessage(5, "잠시 후에 다시 시도하여 주시길 바랍니다.");
                        return;
                    }
                    cm.resetMap(240070210);
                    cm.warp(240070210, "left00");
                    return;
                }
            }
            if (cm.isQuestActive(3774) == true) {
                if (cm.getPlayerCount(240070220) != 0) {
                    cm.getPlayer().dropMessage(5, "잠시 후에 다시 시도하여 주시길 바랍니다.");
                    return;
                }
                cm.resetMap(240070220);
                cm.warp(240070220, "left00");
                return;
            }
            cm.warp(240070100 + (selection * 100), 1);
            break;
        }
    }
}