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
            if (qm.isQuestActive(23264) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("구해 오셨군요~! 구해 오실 줄 알았어요.");
            break;
        }
        case 1: {
            qm.dispose();
            if (qm.getPlayerCount(931050211) != 0) {
                qm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            qm.resetMap(931050211);
            qm.warp(931050211, "enter");
            break;
        }
    }
}