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
            if (cm.isQuestActive(6410) == false) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("그럼 지금 바로 델리를 구하러 가볼래?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(925010000, 0);
            break;
        }
    }
}