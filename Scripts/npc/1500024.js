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
            if (cm.isQuestActive(32123) == false) {
                cm.dispose();
                return;
            }
            cm.askAcceptDecline("#r요정 도시#k를 구하기 위해 이동합니다.\r\n\r\n#b(파티(1~6인)만 입장 가능 / 레벨 : 30 이상)#k");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getPlayerCount(101073010) != 0) {
                cm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            if (cm.getParty() == null) {
                return;
            }
            if (cm.isLeader() == false) {
                return;
            }
            cm.resetMap(101073010);
            cm.warpParty(101073010);
            break;
        }
    }
}