function enter(pi) {
    if (pi.isQuestActive(2621) == true) {
        if (pi.getPlayerCount(910350220) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.warp(910350220, "out00");
        return;
    }
    if (pi.isQuestActive(2639) == true) {
        if (pi.getPlayerCount(910350230) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.warp(910350230, "out00");
        return;
    }
    if (pi.isQuestActive(2643) == true) {
        if (pi.getPlayerCount(910350240) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.warp(910350240, "out00");
        return;
    }
    pi.warp(103000003, "out00");
}