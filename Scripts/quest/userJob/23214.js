var status = -1;

function start(mode, type, selection) {
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
            qm.sendNext("자, 갑니다! 거기에 있는 저는 소환수 모습이 아닌 과거 제 모습입니다.");
            break;
        }
        case 1: {
            qm.sendNextPrev("물론 제가 만들어 낸 형상이기에 과거의 저만큼 강하진 않지만, 그 차원은 제 영역 안이므로 충분히 강할 겁니다. 참고로 다시 들어가려면 퀘스트를 포기하셨다가 다시 시작하셔야 돼요!");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getPlayerCount(931050120) != 0) {
                qm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            qm.forceStartQuest();
            qm.resetMap(931050120);
            qm.warp(931050120, "enter");
            break;
        }
    }
}

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
            qm.sendYesNo("수련은 어떠셨습니까? 성과가 있으시면 지금 상태를 각인 하도록 하겠습니다.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.changeJob(3111);
            break;
        }
    }
}