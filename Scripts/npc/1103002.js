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
            if (cm.isQuestActive(20807) == true) {
                cm.sendYesNo("지금 바로 입단 첫 번째 입단 시험에 도전 할 건가요?");
                return;
            }
            cm.dispose();
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(913070800) != 0) {
                cm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            cm.resetMap(913070800);
            cm.warp(913070800, 0);
            break;
        }
    }
}