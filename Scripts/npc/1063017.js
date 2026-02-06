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
            if (cm.isQuestActive(21734) == false) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("환영합니다. 프란시스 주인님. 주인님의 동굴로 들여 보내 드릴까요?");
            break;
        }
        case 1: {
            cm.sendNext("\r\n이용해 주셔서 감사합니다, 프란시스 주인님.");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getPlayerCount(910510202) != 0) {
                return;
            }
            cm.resetMap(910510202);
            cm.warp(910510202, "out00");
            break;
        }
    }
}