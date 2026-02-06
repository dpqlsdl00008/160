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
            if (cm.getQuestStatus(20406) < 1) {
                return;
            }
            if (cm.getPlayerCount(913030000) != 0) {
                cm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            cm.forceCompleteQuest(20406);
            cm.resetMap(913030000);
            cm.getMap(913030000).spawnNpc(1104002, new java.awt.Point(-273, 88));
            cm.warp(913030000, 0);
            break;
        }
    }
}