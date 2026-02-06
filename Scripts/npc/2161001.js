var status = -1;

function start() {
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
            if (cm.isQuestActive(3173) == true || cm.isQuestActive(3175) == true) {
                cm.warp(211070200, "out00");
                return;
            }
            if (cm.isQuestActive(3178) == true) {
                if (cm.getPlayerCount(921140000) != 0) {
                    cm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                    return;
                }
                cm.resetMap(921140000);
                cm.warp(921140000, "out00");
                cm.getPlayer().dropMessage(5, "몬스터로부터 이피아를 보호하며 알현실까지 가자.");
                return;
            }
            break;
        }
    }
}