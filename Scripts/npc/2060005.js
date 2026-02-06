var status = -1;

function start() {
    if (cm.isQuestActive(6002) == true) {
        status = -1;
    }
    if (cm.isQuestFinished(6003) == true) {
        status = 1;
    }
    action(1, 0, 0);
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
            cm.dispose();
            if (cm.getPlayerCount(923010000) != 0) {
                cm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            cm.resetMap(923010000);
            cm.warp(923010000, 0);
            break;
        }
        case 1: {
            cm.dispose();
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getPlayerCount(922200000) != 0) {
                cm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            cm.resetMap(922200000);
            cm.warp(922200000, 0);
            break;
        }
    }
}