var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
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
            cm.dispose();
            if (cm.isQuestActive(20748) == true) {
                cm.topMessage("잊혀진 회랑 탐색 완료");
                cm.forceStartQuest(20750, "완료");
            }
            break;
        }
    }
}