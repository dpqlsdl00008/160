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
            if (qm.isQuestActive(31351) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.lockInGameUI(true);
            qm.sendNextS("#b(타란튤로스를 해치우자 암벽 거인의 떨림이 잦아 들고 맑은 기운이 사방에서 샘 솟기 시작한다.)#k\r\n암벽 거인을 위기로부터 구출해 냈다. 이제 당분간은 오염 되지 않을 것 같다.", 17);
            break;
        }
        case 1: {
            qm.sendNextPrevS("암벽 거인에게 되돌아 가 상태를 살펴 보자.", 17);
            break;
        }
        case 2: {
            qm.dispose();
            qm.lockInGameUI(false);
            qm.forceCompleteQuest();
            break;
        }
    }
}