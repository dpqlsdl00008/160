var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 1) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (qm.isQuestFinished(24004) == false) {
                qm.dispose();
                qm.forceStartQuest();
                break;
            } else {
                qm.sendNext("어쩌면 행운일지도 모르겠습니다. 검은 마법사로 인해 황폐해진 메이플 월드로부터, 우리는 잠시 떠나 있는 것과 마찬가지니까요. 우리가 다시 돌아올 때 이 땅이 어떻게 변해 있을까요...");
                break;
            }
        }
        case 1: {
            qm.sendAcceptDecline("더 아름다운 세상이 되어 있길 기다리면서 먼저 잠을 청하겠습니다, 메르세데스님...");
            qm.gainItem(2431876, 1);
            qm.gainItem(2431877, 1);
            break;
        }
        case 2: {
            qm.dispose();
            qm.sendNext("부디, 좋은 꿈... 꾸시길.");
            qm.forceCompleteQuest();
            break;
        }
    }
}