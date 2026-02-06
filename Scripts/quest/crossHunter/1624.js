var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (qm.isQuestActive(1624) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("오랜만이야. 나 기억하지? 기억 안나? 왜 전에 오르비스 구름 공원에서 만났었는데... 이제 기억났어?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}