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
            if (qm.isQuestActive(1616) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("당신이로군요. 어서 오세요. 자세한 이야기는 조용한 곳으로 가서 하도록 하죠.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.warp(931050500, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}